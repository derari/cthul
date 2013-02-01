package org.cthul.parser.rule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.cthul.parser.lexer.ScanException;

/**
 * Syntax
 * Strings:     "abc", 'abc'
 * Keys:        abc
 * Choice:      A | B
 * Group:       ( A )
 * Look ahead:  &A
 * Anti match:  !A
 * Priority:    A^ A. A~ A# A[1]
 */
public class RuleParser {
    
    protected static final char EOI = '\0';
    
    protected final String string;
    protected final int start;
    protected final int end;
    protected final String defSymbol;
    protected final int defPriority;
    protected int position = -1;
    protected final List<String> keys = new ArrayList<>();

    public RuleParser(String string, String defSymbol, int defPriority) {
        this(string, 0, string.length(), defSymbol, defPriority);
    }
    
    public RuleParser(String string, int start, int end, String defSymbol, int defPriority) {
        this.string = string;
        this.start = start;
        this.end = end;
        this.defSymbol = defSymbol;
        this.defPriority = defPriority;
    }

    public Rule parse() {
        position = start;
        return parseSubrule();
    }
    
    protected boolean atEnd() {
        return position >= end;
    }
    
    protected boolean atGroupEnd() {
        return currentAfterWS() == ')' || atEnd();
    }
    
    protected char charAt(int i) {
        if (i < start || i >= end) return EOI;
        char c = string.charAt(i);
        if (Character.isWhitespace(c)) return ' ';
        return c;
    }
    
    protected char current() {
        return charAt(position);
    }
    
    protected char currentAfterWS() {
        char c = current();
        while (c == ' ') c = next();
        return c;
    }
    
    protected char next() {
        if (atEnd()) return EOI;
        position++;
        return current();
    }
    
    protected char nextAfterWS() {
        next();
        return currentAfterWS();
    }
    
    protected char peek() {
        return charAt(position+1);
    }

    protected Rule parseSubrule() {
        List<Rule> steps = parseSteps();
        if (steps.size() == 1) {
            return steps.get(0);
        } else {
            return new ProductionRule(steps);
        }
    }

    protected Rule parseProduction() {
        List<Rule> steps = parseSteps();
        return new ProductionRule(steps);
    }
    
    protected Rule parseAntiMatch() {
        assert current() == '!';
        if (nextAfterWS() == '(') {
            nextAfterWS();
            List<Rule> steps = parseSteps();
            if (steps.isEmpty()) throw parseException("Empty rule", null);
            skipExpected(')');
            return new AntiMatchRule(steps);
        } else {
            Rule match = parseStep();
            return new AntiMatchRule(match);
        }
    }

    protected Rule parseLookAhead() {
        assert current() == '&';
        if (nextAfterWS() == '(') {
            nextAfterWS();
            List<Rule> steps = parseSteps();
            if (steps.isEmpty()) throw parseException("Empty rule", null);
            skipExpected(')');
            return new LookAheadRule(steps);
        } else {
            Rule match = parseStep();
            return new LookAheadRule(match);
        }
    }

    protected Rule parseNoResult() {
        assert current() == ':';
        if (nextAfterWS() == '(') {
            nextAfterWS();
            List<Rule> steps = parseSteps();
            if (steps.isEmpty()) throw parseException("Empty rule", null);
            skipExpected(')');
            return new NoResultRule(steps);
        } else {
            Rule match = parseStep();
            return new NoResultRule(match);
        }
    }

    protected Rule parseGroup() {
        assert current() == '(';
        next();
        Rule r = parseSubrule();
        if (r == null) {
            throw parseException("Empty rule", null);
        }
        skipExpected(')');
        return r;
    }
    
    protected void skipExpected(char c) {
        if (current() != c) {
            throw parseException("Expected '" + c + "'", null);
        }
        next();
    }
    
    protected List<Rule> parseSteps() {
        final List<Rule> steps = new ArrayList<>();
        while (!atGroupEnd()) {
            Rule s = parseStep();
            if (s == null) break;
            steps.add(s);
        }
        return steps;
    }
    
    protected Rule parseStep() {
        Rule r = parseStepWithoutQuantifier();
        switch (current()) {
            case '?':
                // return OptionalRule(r);
        }
        return r;
    }
    
    protected Rule parseStepWithoutQuantifier() {
        switch (current()) {
            case '!': return parseAntiMatch();
            case '&': return parseLookAhead();
            case ':': return parseNoResult();
            case '(': return parseGroup();
            case '"': return parseString('"');
            case '\'': return parseString('\'');
        }
        return parseKey();
    }
    
    protected Rule parseString(char q) {
        int sStart = position+1;
        int sEnd = scanString(q);
        return new StringMatchRule(string.substring(sStart, sEnd).replaceAll("\\\\(.)", "$1"));
    }
    
    protected Rule parseKey() {
        assert current() != ' ' : "ws should be skipped";
        int kStart, kEnd;
        if (current() == '`') {
            kStart = position+1;
            kEnd = scanString('`');
        } else {
            kStart = position;
            kEnd = scanSymbol();
        }
        if (kStart >= kEnd) throw parseException("Symbol expected", null);
        String key = string.substring(kStart, kEnd);
        int priority = parsePriority(key);
        return (Rule) (Object) new KeyMatchRule(key, priority);
    }
    
    protected int parsePriority(String sym) {
        switch (current()) {
            case '^': 
                next();
                return defPriority+1;
            case '.': 
                next();
                return defPriority;
            case '#': 
                next();
                return 0;
            case '~':
                next();
                break;
            case '[':
                int pStart = position+1;
                char c = next();
                if (c == '-' || c == '+') c = next();
                while ('0' <= c && c <= '9') c = next();
                if (c != ']') throw parseException(pStart, "Expected ']'", null);
                int pEnd = position;
                next();
                try {
                    return Integer.parseInt(string.substring(pStart, pEnd));
                } catch (NumberFormatException e) {
                    throw parseException(pStart, null, e);
                }
        }
        return sym.equals(defSymbol) ? defPriority : 0;
    }
    
    private static final char[] NON_KEY_CHARS = {
        '\0', ' ', '(', ')', '[', ']', '{', '}', '<', '>', ',',
        '!', '&', '?', ':', '+', '*', '.', '^', '~', '#'};
    static { Arrays.sort(NON_KEY_CHARS); }
    
    protected boolean isKeyCharacter(char c) {
        return Arrays.binarySearch(NON_KEY_CHARS, c) < 0;
    }

    protected int scanString(char q) {
        while (true) {
            char c = next();
            if (c == '\\') {
                next();
            } else if (c == q) {
                break;
            }
        }
        int p = position;
        next();
        return p;
    }
    
    protected int scanSymbol() {
        char c = current();
        while (isKeyCharacter(c)) {
            c = next();
        }
        return position;
    }
    
    protected RuntimeException parseException(String message, Throwable cause) {
        return new ScanException(null, string, position, message, cause);
    }
    
    protected RuntimeException parseException(int lastMatchStart, String message, Throwable cause) {
        return parseException(lastMatchStart, position, message, cause);
    }
    
    protected RuntimeException parseException(int lastMatchStart, int position, String message, Throwable cause) {
        String lastMatch = string.substring(lastMatchStart, position);
        return new RuleParserException(lastMatch, string, position, message, cause);
    }
    
    public class RuleParserException extends ScanException {
        public RuleParserException(Object lastMatch, String input, int position, String message, Throwable cause) {
            super(lastMatch, input, position, message, cause);
        }
    }
    
}
