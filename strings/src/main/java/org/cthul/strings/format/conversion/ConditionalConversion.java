package org.cthul.strings.format.conversion;

import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;
import org.cthul.strings.format.*;

/**
 *
 * @author Arian Treffer
 */
public class ConditionalConversion extends FormatConversionBase {

    public static final ConditionalConversion INSTANCE = new ConditionalConversion();
    
    public void register(FormatterConfiguration conf) {
        conf.setShortFormat('f', this);
        conf.setLongFormat("Case", this);
    }
    
    @Override
    public int format(FormatterAPI formatter, Object value, Locale locale, String flags, int width, int precision, String formatString, int position) throws IOException {
        ensureNoWidth(width);
        ensureNoPrecision(precision);
        ensureNoFlags(flags);
        return new Conditional(formatter, value, formatString, position).apply() - position;
    }
    
    public static class Conditional {
        
        protected final FormatterAPI api;
        protected final Object value;
        protected final String string;
        protected int p;
        protected boolean firstCondition = true;
        protected boolean eval = true;

        public Conditional(FormatterAPI api, Object value, String string, int position) {
            this.api = api;
            this.value = value;
            this.string = string;
            this.p = position;
        }
        
        public int apply() throws IOException {
            while (nextConditional()) firstCondition = false;
            if (firstCondition) {
                throw new IllegalArgumentException("Conditional expected.");
            }
            if (!atEnd() && peekNext() == ';') next();
            return p;
        }
        
        private static final String NUM_CHARS = "+-0123456789.abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_";
        protected static final char[] EXPRESSION_START = flags("(<>=!?" + NUM_CHARS);
        protected static final char[] OPEN_BRACKET = flags("(<[{");
        protected static final char[] NUMBER_CHARS = flags(NUM_CHARS);
        
        protected char peekNext() {
            return string.charAt(p);
        }

        protected char peekNext(int i) {
            return string.charAt(p + i);
        }

        protected char next() {
            return string.charAt(p++);
        }
        
        protected void prev() {
            p--;
        }
        
        protected boolean atEnd() {
            return p >= string.length();
        }

        protected boolean nextConditional() throws IOException {
            if (atEnd()) return false;
            boolean nextConditionalExpected = true;
            final boolean match;
            char c = peekNext();
            if (c == ';') return false;
            if (oneOf(c, EXPRESSION_START)) {
                match = parseCondition(eval);
            } else {
                if (firstCondition) {
                    match = defaultEval(eval);
                } else {
                    // else-conditional
                    if (c == ':') next();
                    else if (!oneOf(c, OPEN_BRACKET)) return false;
                    match = true;
                    nextConditionalExpected = false;
                }
            }
            parseValue(eval && match);
            if (match) eval = false;
            return nextConditionalExpected;
        }
        
        // A note about parse methods:
        // The return value is true if the subtree evaluated to true.
        // When the `eval` parameter is false, actual evaluation is skipped
        // and the return values are arbitrary.

        protected boolean parseCondition(boolean eval) {
            char c = peekNext();
            if (oneOf(c, EXPRESSION_START)) {
                return parseJunction(eval);
            } else {
                return defaultEval(eval);
            }
        }
        
        protected boolean defaultEval(boolean eval) {
            if (!eval) return false;
            if (value == null) return false;
            if (value instanceof Boolean) return (Boolean) value;
            if (value instanceof Number) {
                double d = ((Number) value).doubleValue();
                return d != 0 && !Double.isNaN(d);
            }
            return true;
        }
        
        protected boolean parseJunction(boolean eval) {
            boolean match = false;
            do {
                match |= parseConjunction(eval);
                if (atEnd()) return match;
                eval = eval && !match;
            } while (next() == '|');
            prev();
            return match;
        }
        
        protected boolean parseConjunction(boolean eval) {
            boolean match = true;
            do {
                match &= parseExpression(eval);
                if (atEnd()) return match;
                eval = eval && match;
            } while (next() == '&');
            prev();
            return match;
        }
        
        protected boolean parseExpression(boolean eval) {
            if (atEnd()) {
                throw FormatException.illegalArgument(
                        "End of string, expected conditional expression.");                
            }
            switch (next()) {
                case '!': return !parseExpression(eval);
                case '?': return parseNullCheck(eval);
                case '(': return parseBrackets(eval);
            }
            prev();
            return parseComparison(eval);
        }
        
        protected boolean parseNullCheck(boolean eval) {
            if (atEnd()) return value != null;
            final boolean match;
            if (peekNext() == '?') {
                next();
                match = defaultEval(eval);
            } else {
                match = value != null;
            }
            if (oneOf(peekNext(), EXPRESSION_START)) {
                eval = eval && match;
                return parseExpression(eval) & match;
            }
            return match;
        }
        
        protected boolean parseBrackets(boolean eval) {
            boolean match = parseJunction(eval);
            if (atEnd() || next() != ')') {
                throw new IllegalArgumentException("Closing ')' expected.");
            }
            return match;
        }
        
        protected boolean parseComparison(boolean eval) {
            boolean eq = false, gt = false, lt = false;
            loop: while (true) {
                if (atEnd()) {
                    throw FormatException.illegalArgument(
                            "End of string, expected comparison or number.");
                }
                switch (next()) {
                    case '<': lt = true; break;
                    case '>': gt = true; break;
                    case '=': eq = true; break;
                    default: break loop;
                }
            }
            prev();
            eq |= !lt && !gt; // no explicit comparison given, default eq
            
            if (!eval) {
                searchNumberEnd();
                return false;
            }
            
            if (value == null) throw new NullPointerException();
            Object v = (value instanceof Boolean) ? ((Boolean) value ? 1 : 0) : value;
            if (!(v instanceof Number)) {
                throw FormatException.illegalArgument(
                        "Expected Number or Boolean argument, got %s.", v);
            }
            if (v instanceof Float || v instanceof Double) {
                return compare(((Number) v).doubleValue(), eq, gt, lt);
            } else {
                return compare(((Number) v).longValue(), eq, gt, lt);
            }
        }

        protected boolean compare(double value, boolean eq, boolean gt, boolean lt) {
            String num = string.substring(p, searchNumberEnd());
            boolean hex = num.length() > 2 && num.charAt(1) == 'x';
            double other = hex ? Long.parseLong(num.substring(2), 16) : Double.parseDouble(num);
            if (value == other || 
                    (Double.isNaN(value) && Double.isNaN(other))) {
                return eq;
            } else if (value < other) {
                return lt;
            } else if (value > other) {
                return gt;
            }
            return false;
        }

        protected boolean compare(long value, boolean eq, boolean gt, boolean lt) {
            String num = string.substring(p, searchNumberEnd());
            boolean hex = num.length() > 2 && num.charAt(1) == 'x';
            long other = hex ? Long.parseLong(num.substring(2), 16) : Long.parseLong(num);
            if (value == other) {
                return eq;
            } else if (value < other) {
                return lt;
            } else if (value > other) {
                return gt;
            }
            return false;
        }

        protected int searchNumberEnd() {
            char c;
            do {
                if (atEnd()) return p;
                c = next();
            } while (oneOf(c, NUMBER_CHARS));
            prev();
            return p;
        }

        protected void parseValue(boolean apply) throws IOException {
            if (atEnd()) {
                throw FormatException.illegalArgument(
                        "End of string, expected value.");
            }
            char delim = next();
            int delimLen = scanDelimiter(delim);
            final int start = p;
            int end = searchValueEnd(delimLen, closeDelimiter(delim));
            if (apply) api.format(string, start, end);
        }

        protected int scanDelimiter(char delim) {
            int delimLen = 0;
            do {
                delimLen++;
                if (atEnd()) return delimLen;
            } while (next() == delim);
            prev();
            return delimLen;
        }
        
        protected char closeDelimiter(char delim) {
            switch (delim) {
                case '(': return ')';
                case '[': return ']';
                case '{': return '}';
                case '<': return '>';
                default: return delim;
            }
        }

        protected int searchValueEnd(int delimLen, char delim) throws IllegalArgumentException {
            int closeLen = delimLen;
            while (!atEnd() && closeLen > 0) {
                if (next() == delim) closeLen--;
                else closeLen = delimLen;
            }
            if (closeLen > 0) {
                throw FormatException.illegalArgument(
                        "Expected %d closing '%s', got %d at end of string", 
                        delimLen, delim, closeLen);
            }
            final int end = p - delimLen;
            return end;
        }

    }
    
}
