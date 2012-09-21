package org.cthul.strings;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cthul.strings.format.*;
import org.cthul.strings.format.pattern.Standard;

/**
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
    
    private int patternCount = 0;
    private Match[] matches = new Match[5];
    private int nextCaptureBase = 1;

    public FormatPattern(String formatString) {
        this(null, formatString);
    }
    
    @SuppressWarnings("OverridableMethodCallInConstructor")
    public FormatPattern(PatternConfiguration conf, String formatString) {
        this.conf = conf != null ? conf : PatternConfiguration.getDefault();
        StringBuilder regex = new StringBuilder();
        parseFormatString(formatString, regex);
        pattern = Pattern.compile(regex.toString());
    }

    public Pattern getPattern() {
        return pattern;
    }
    
    public Matcher regexMatcher(CharSequence csq) {
        return pattern.matcher(csq);
    }
    
    public FormatMatcher matcher(CharSequence csq) {
        return new FormatMatcher(this, regexMatcher(csq), patternCount, matches);
    }
    
    protected void parseFormatString(String formatString, StringBuilder regex) {
        new Parser(regex, Locale.getDefault()).parse(formatString);
    }
    
    protected void addPattern(ConversionPattern p, int argId) {
        if (patternCount + 1 == matches.length) {
            int newLen = matches.length * 2;
            matches = Arrays.copyOf(matches, newLen);
        }
        matches[patternCount] = new Match(p, nextCaptureBase, argId);
        patternCount++;
    }
    
    protected ConversionPattern getStandardPattern(String formatId) {
        switch (formatId.charAt(0)) {
            case 'd':
                return Standard.DECIMAL;
            case 's':
                return Standard.STRING;
            default:
                throw FormatException.unknownFormat(formatId);
        }
    }
    
    protected class Parser extends FormatStringParser<RuntimeException> {
        
        private final StringBuilder regex;
        private final Locale locale;
        private final API api;

        public Parser(StringBuilder regex, Locale locale) {
            this.regex = regex;
            this.locale = locale;
            this.api = new API(regex);
        }

        @Override
        protected void appendText(CharSequence csq, int start, int end) throws RuntimeException {
            regex.append(Pattern.quote(csq.subSequence(start, end).toString()));
        }

        @Override
        protected void appendPercent() throws RuntimeException {
            regex.append("%");
        }

        @Override
        protected void appendNewLine() throws RuntimeException {
            regex.append("\\n");
        }

        @Override
        protected int customShortFormat(char formatId, int argId, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws RuntimeException {
            ConversionPattern p = conf.getShortFormat(formatId);
            if (p == null) {
                throw FormatException.unknownFormat("i" + formatId);
            }
            return applyPattern(p, argId, flags, width, precision, formatString, lastPosition);
        }

        @Override
        protected int customLongFormat(String formatId, int argId, String flags, int width, int precision, CharSequence formatString, int lastPosition, boolean uppercase) throws RuntimeException {
            ConversionPattern p = conf.getLongFormat(formatId);
            if (p == null) {
                throw FormatException.unknownFormat("j" + formatId);
            }
            return applyPattern(p, argId, flags, width, precision, formatString, lastPosition);
        }

        @Override
        protected int standardFormat(Matcher matcher, String fId, CharSequence formatString) throws RuntimeException {
            final String formatId = getStandardFormatId(fId);
            final int argId = getArgIndex(matcher);
            final String flags = getFlags(matcher);
            final int width = getWidth(matcher);
            final int precision = getPrecision(matcher);
            ConversionPattern p = getStandardPattern(formatId);
            final int end = matcher.end();
            return end + applyPattern(p, argId, flags, width, precision, formatString, end);
        }
        
        @Override
        protected void standardFormat(String formatId, int argId, String flags, int width, int precision) throws RuntimeException {
            throw new UnsupportedOperationException();
        }

        protected int applyPattern(ConversionPattern p, int argId, String flags, int width, int precision, CharSequence formatString, int lastPosition) {
           addPattern(p, argId);
           return p.toRegex(api, locale, flags, width, precision, formatString.toString(), lastPosition);
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
            nextCaptureBase++;
            return this;
        }

        @Override
        public PatternAPI addedCapturingGroups(int i) {
            nextCaptureBase += i;
            return this;
        }

        @Override
        public Object putMemento(Object newMemento) {
            Match match = matches[patternCount-1];
            Object old = match.memento;
            match.memento = newMemento;
            return old;
        }
        
    }
    
    protected class Match {
        
        protected final ConversionPattern pattern;
        protected final int captureBase;
        protected final int argId;
        protected Object memento;

        public Match(ConversionPattern pattern, int captureBase, int argId) {
            this.pattern = pattern;
            this.captureBase = captureBase;
            this.argId = argId;
        }

        public ConversionPattern getPattern() {
            return pattern;
        }

        public int getCaptureBase() {
            return captureBase;
        }

        public int getArgId() {
            return argId;
        }

        public Object getMemento() {
            return memento;
        }
        
    }
    
}
