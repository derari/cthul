package org.cthul.strings;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.strings.format.*;
import org.cthul.strings.format.pattern.StandardPattern;

/**
 * The opposite of {@link Formatter}: takes a string and a format string,
 * and tries to parse the original values.
 * 
 * @author Arian Treffer
 */
public class FormatPattern {
    
    public static List<Object> match(String formatString, String input) {
        return compile(formatString).matcher(input).match();
    }
    
    public static FormatPattern compile(String formatString) {
        return new FormatPattern(formatString);
    }
    
    public static Pattern pattern(String formatString) {
        return compile(formatString).getPattern();
    }
    
    public static String regex(String formatString) {
        return pattern(formatString).pattern();
    }
    
    private final PatternConfiguration conf;
    private final Pattern pattern;
    
    private final PatternData data;

    public FormatPattern(String formatString) {
        this(null, formatString);
    }
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public FormatPattern(PatternConfiguration conf, String formatString) {
        this.conf = conf != null ? conf : PatternConfiguration.getDefault();
        StringBuilder regex = new StringBuilder();
        data = new API(regex).parse(null, formatString);
        pattern = Pattern.compile(regex.toString());
    }

    public Pattern getPattern() {
        return pattern;
    }
    
    public Matcher regexMatcher(CharSequence csq) {
        return pattern.matcher(csq);
    }
    
    public FormatMatcher matcher(CharSequence csq) {
        Matcher m = regexMatcher(csq);
        return new FormatMatcher(this, m, data);
    }
    
    protected ConversionPattern getStandardPattern(String formatId) {
        switch (formatId.charAt(0)) {
            case 'd':
                return StandardPattern.DECIMAL;
            case 's':
                return StandardPattern.STRING;
            default:
                throw FormatException.unknownFormat(formatId);
        }
    }
    
    protected class Parser extends FormatStringParser<RuntimeException> {
        
        protected final PatternData.Builder pattern;
        protected final Locale locale;

        public Parser(PatternAPI api) {
            this.pattern = new PatternData.Builder(api);
            Locale l = conf.locale();
            this.locale = l;
        }

        @Override
        protected Object getArg(int i) {
            // careful! format strings use one-based indices
            return i-1;
        }

        @Override
        protected Object getArg(char c) {
            return c;
        }

        @Override
        protected Object getArg(String s) {
            return s;
        }

        @Override
        protected void appendText(CharSequence csq, int start, int end) throws RuntimeException {
            pattern.append(Pattern.quote(csq.subSequence(start, end).toString()));
        }

        @Override
        protected void appendPercent() throws RuntimeException {
            pattern.append("%");
        }

        @Override
        protected void appendNewLine() throws RuntimeException {
            pattern.append("\\n");
        }

        @Override
        protected int customShortFormat(char formatId, Object arg, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws RuntimeException {
            ConversionPattern p = conf.getShortFormat(formatId);
            if (p == null) {
                throw FormatException.unknownFormat("i" + formatId);
            }
            return applyPattern(p, arg, flags, width, precision, formatString, lastPosition);
        }

        @Override
        protected int customLongFormat(String formatId, Object arg, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws RuntimeException {
            ConversionPattern p = conf.getLongFormat(formatId);
            if (p == null) {
                throw FormatException.unknownFormat("j" + formatId);
            }
            return applyPattern(p, arg, flags, width, precision, formatString, lastPosition);
        }

        @Override
        protected int standardFormat(Matcher matcher, String fId, CharSequence formatString) throws RuntimeException {
            final String formatId = getStandardFormatId(fId);
            final Object arg = getArg(formatString, matcher, G_ARG_ID);
            final String flags = getFlags(matcher);
            final int width = getWidth(matcher);
            final int precision = getPrecision(matcher);
            ConversionPattern p = getStandardPattern(formatId);
            final int end = matcher.end();
            return end + applyPattern(p, arg, flags, width, precision, formatString, end);
        }
        
        @Override
        protected void standardFormat(String formatId, Object arg, String flags, int width, int precision) throws RuntimeException {
            throw new UnsupportedOperationException();
        }

        protected int applyPattern(ConversionPattern p, Object arg, String flags, int width, int precision, CharSequence formatString, int lastPosition) {
            return pattern.addConversion(p, arg, locale, flags, width, precision, formatString.toString(), lastPosition);
        }
        
        protected PatternData patternData() {
            return pattern.data();
        }
    
   }
    
    protected class API implements PatternAPI {
        
        private final StringBuilder regex;

        public API(StringBuilder regex) {
            this.regex = regex;
        }

        @Override
        public PatternAPI append(char c) {
            regex.append(c);
            return this;
        }

        @Override
        public PatternAPI append(CharSequence csq) {
            regex.append(csq);
            return this;
        }

        @Override
        public PatternAPI append(CharSequence csq, int start, int end) {
            regex.append(csq, start, end);
            return this;
        }

        @Override
        public PatternAPI addedCapturingGroup() { 
            return this;
        }

        @Override
        public PatternAPI addedCapturingGroups(int i) { 
            return this; 
        }

        @Override
        public Object putMemento(Object newMemento) {
            throw new UnsupportedOperationException();
        }

        @Override
        public PatternData parse(PatternAPI api, String format) {
            return parse(api, format, 0, format.length());
        }

        @Override
        public PatternData parse(PatternAPI api, String format, int start, int end) {
            if (api == null) api = this;
            Parser parser = new Parser(api);
            parser.parse(format, start, end);
            return parser.patternData();
        }
        
    }
    
}
