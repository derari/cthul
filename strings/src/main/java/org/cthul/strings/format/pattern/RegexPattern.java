package org.cthul.strings.format.pattern;

import java.util.Locale;
import java.util.regex.*;
import org.cthul.strings.format.PatternAPI;
import org.cthul.strings.format.PatternConfiguration;

/**
 * Allows to use regular expressions in a format string.
 * <p>
 * Example: {@code "%iR2/(a*)(\\d+)b/}
 * <p>
 * The format specifier ({@code %iR}) is followed by an optional group index 
 * (default 0), or {@code m} to obtain a match result.
 * The pattern is enclosed by {@code /}. Within the pattern, all occourences of
 * {@code /} have to be escaped with a backslash.
 * 
 * @author Arian Treffer
 */
public class RegexPattern extends FormatPatternBase {

    public static final RegexPattern INSTANCE = new RegexPattern();
    
    public void register(PatternConfiguration conf) {
        conf.setShortFormat('R', this);
        conf.setLongFormat("Regex", this);
    }
    
    @Override
    public int toRegex(PatternAPI pattern, Locale locale, String flags, int width, int precision, final String formatString, final int position) {
        Memento memento = new Memento();
        pattern.putMemento(memento);
        final int length = formatString.length();
        int n = position;
        if (n < length && formatString.charAt(n) != '/') {
            n = scanGroupIndex(memento, n, formatString, length);
        }
        n = scanRegex(n, length, formatString, pattern, memento);
        return n - position + 1;
    }

    @Override
    public Object parse(Matcher matcher, int capturingBase, Object memento, Object lastArgValue) {
        return ((Memento) memento).getResult(matcher, capturingBase);
    }

    private int scanRegex(int n, final int length, final String formatString, PatternAPI pattern, Memento memento) throws IllegalArgumentException {
        if (n >= length) {
            throw new IllegalArgumentException("'/' expected, but end of string");
        }
        if (formatString.charAt(n) != '/') {
            throw new IllegalArgumentException("'/' expected, but got " +
                    formatString.charAt(n));
        }

        final int start = n;
        char c = 0;
        while (c != '/') {
            n++;
            if (n >= length) {
                throw new IllegalArgumentException(
                        "Closing '/' expected, but end of string");
            }
            c = formatString.charAt(n);
            if (c == '\\') {
                n++;
                if (n >= length) {
                    throw new IllegalArgumentException(
                            "Closing '/' expected, but end of string");
                }
                c = formatString.charAt(n);
            }
        }
        String regex = formatString.substring(start+1, n);
        regex = regex.replaceAll("(^|[^\\\\])\\/", "$1/"); // unescape '/'
        memento.setPattern(regex);
        int groupCount = Pattern.compile(regex).matcher("").groupCount();
        memento.setGroupCount(groupCount);
        pattern.addedCapturingGroups(groupCount);

        pattern.append('(');
        pattern.append(regex);
        pattern.append(')');
        pattern.addedCapturingGroup();

        return n;
    }

    private int scanGroupIndex(Memento memento, int n, final String formatString, final int length) throws NumberFormatException {
        final int start = n;
        char c = formatString.charAt(n);
        if (c == 'm') {
            memento.setGroupIndex(-1);
            return n+1;
        }
        while ('0' <= c && c <= '9') {
            n++;
            c = n < length ? 0 : formatString.charAt(n);
        }
        if (n > start) {
            int groupIndex = Integer.parseInt(formatString.substring(start, n));
            memento.setGroupIndex(groupIndex);
        }
        return n;
    }
    
    protected static class Memento {
        
        protected int groupIndex;
        protected int groupCount;
        protected String pattern;

        public void setGroupCount(int groupCount) {
            this.groupCount = groupCount;
        }

        public void setGroupIndex(int groupIndex) {
            this.groupIndex = groupIndex;
        }

        public void setPattern(String pattern) {
            this.pattern = pattern;
        }
        
        public Object getResult(Matcher matcher, int capturingBase) {
            if (groupIndex < 0) {
                return createMatchReuslt(matcher, capturingBase);
            } else {
                return matcher.group(capturingBase + groupIndex);
            }
        }

        protected Object createMatchReuslt(Matcher matcher, int capturingBase) {
            return new OffsetMatchResult(matcher.toMatchResult(), capturingBase, groupCount, pattern);
        }
    }
    
    protected static class OffsetMatchResult implements MatchResult {
        
        private final MatchResult match;
        private final int offset;
        private final int groupCount;
        private final String pattern;

        public OffsetMatchResult(MatchResult match, int offset, int groupCount, String pattern) {
            this.match = match;
            this.offset = offset;
            this.groupCount = groupCount;
            this.pattern = pattern;
        }
        
        protected void ensureRange(int group) {
            if (group < 0 || group > groupCount) {
                throw new IllegalArgumentException(
                        "Expected 0 <= g <= " + groupCount + 
                        ", but group = " + group);
            }
        }

        @Override
        public int start() {
            return match.start(offset);
        }

        @Override
        public int start(int group) {
            ensureRange(group);
            return match.start(offset + group);
        }

        @Override
        public int end() {
            return match.end(offset);
        }

        @Override
        public int end(int group) {
            ensureRange(group);
            return match.end(offset + group);
        }

        @Override
        public String group() {
            return match.group(offset);
        }

        @Override
        public String group(int group) {
            ensureRange(group);
            return match.group(offset + group);
        }

        @Override
        public int groupCount() {
            return groupCount;
        }

        @Override
        public String toString() {
            try {
                return "MatchResult [Pattern=" + pattern + 
                        ", Group 0/" + groupCount + "=" + group(0) + "]";
            } catch (RuntimeException e) {
                return "Invalid MatchResult [" + e.getMessage() + "]";
            }
        }
        
    }
    
}
