package org.cthul.strings.format.pattern;

import java.util.Locale;
import java.util.regex.Matcher;
import org.cthul.strings.format.MatcherAPI;
import org.cthul.strings.format.PatternAPI;

/**
 *
 * @author Arian Treffer
 */
public abstract class PatternAlignmentBase extends FormatPatternBase {
    
    protected static final int IGNORE_SUBPATTERN = Integer.MIN_VALUE;
    
    private static final char[] F_ALL = flags(F_JUSTIFICATION + F_PADDING);
    private static final char[] F_PAD = flags(F_PADDING);
    
    protected char[] getValidFlags() {
        return F_ALL;
    }
    
    protected char[] getPaddingFlags() {
        return F_PAD;
    }
    
    protected char getDefaultPaddingChar() {
        return ' ';
    }
    
    protected char getPaddingChar(String flags) {
        char p = selectFlag(flags, getPaddingFlags());
        if (p != 0) return p;
        return getDefaultPaddingChar();
    }

    protected void appendPadding(final PatternAPI pattern, final char padding) {
        pattern.append("[");
        if (padding == ']') pattern.append('\\');
        pattern.append(padding);
        pattern.append("]*");
    }
    
    protected void appendPadding(final PatternAPI pattern, final char padding, int w) {
        if (w <= 0) return;
        pattern.append("[");
        if (padding == ']') pattern.append('\\');
        pattern.append(padding);
        pattern.append("]{");
        pattern.append(String.valueOf(w));
        pattern.append("}");
    }
    
    protected boolean matchExactWidth(String flags, int width) {
        return false;
    }

    @Override
    public int toRegex(PatternAPI pattern, Locale locale, String flags, int width, int precision, String formatString, int position) {
        ensureValidFlags(flags, getValidFlags());
        Justification j = getJustification(flags, width);
        char padding = j == Justification.None ? 0 : getPaddingChar(flags);
        if (j != Justification.None && matchExactWidth(flags, width)) {
            return toExactWidthRegex(pattern, locale, flags, j, padding, width, precision, formatString, position);
        }
        switch (j) {
            case Center:
            case Right:
                appendPadding(pattern, padding);
        }
        pattern.addedCapturingGroup();
        pattern.append('(');
        int result = toRegex(pattern, locale, flags, padding, precision, formatString, position);
        pattern.append(')');
        switch (j) {
            case Left:
            case Center:
                appendPadding(pattern, padding);
        }
        return result;
    }
    
    /**
     * Appends a pattern to {@code pattern} that will be parsed into a value.
     * @param pattern
     * @param locale
     * @param flags
     * @param padding padding that is expected to surround the match (should not be matched here).
     * @param precision
     * @param formatString
     * @param position
     * @return number of additional characters consumend from format string
     */
    protected abstract int toRegex(PatternAPI pattern, Locale locale, String flags, char padding, int precision, String formatString, int position);
    
    protected int toExactWidthRegex(PatternAPI pattern, Locale locale, String flags, Justification j, char padding, int width, int precision, String formatString, int position) {
        WidthMatchSubpatterns sub = createSubpatterns(pattern, locale, flags, j, padding, width, precision);
        sub.open();

        sub.paddingWidth = -1;
        int result = appendExactWidthRegex(sub, locale, flags, Justification.None, padding, -1, precision, formatString, position);
        for (int w = 0; w <= width; w++) {
            sub.paddingWidth = width - w;
            int r = appendExactWidthRegex(sub, locale, flags, j, padding, w, precision, formatString, position);
            if (r != IGNORE_SUBPATTERN) result = r;
        }

        sub.close();
        pattern.putMemento(sub);
        return result;
    }
    
    protected WidthMatchSubpatterns createSubpatterns(PatternAPI pattern, Locale locale, String flags, Justification j, char padding, int width, int precision) {
        return new WidthMatchSubpatterns(pattern, width, j, padding);
    }
    
    protected int appendExactWidthRegex(WidthMatchSubpatterns sub, Locale locale, String flags, Justification j, char padding, int width, int precision, String formatString, int position) {
        sub.beginSubpattern();
        int r = toRegex(sub.api(), locale, flags, padding, width, precision, formatString, position);
        if (r == IGNORE_SUBPATTERN) sub.ignoreSubpattern();
        else sub.allowEmptySubpattern();
        sub.endSubpattern();
        return r;
    }

    /**
     * Appends a pattern to {@code pattern} that will be parsed into a value.
     * If {@code with} is non-negative, it specifies the exact number of 
     * characters the pattern should consume.
     * @param regex
     * @param locale
     * @param flags
     * @param padding
     * @param width
     * @param precision
     * @param formatString
     * @param position
     * @return 
     */
    protected int toRegex(PatternAPI regex, Locale locale, String flags, char padding, int width, int precision, String formatString, int position) {
        throw new UnsupportedOperationException("Explicit width matching not supported.");
    }

    @Override
    public Object parse(MatcherAPI matcherAPI, Matcher matcher, int capturingBase, Object memento, Object lastArgValue) {
        if (memento instanceof WidthMatchSubpatterns) {
            WidthMatchSubpatterns sub = (WidthMatchSubpatterns) memento;
            int n = sub.getMatchingPattern(matcher, capturingBase);
            capturingBase += sub.getCapturingOffset(n);
            Object m = sub.getMemento(n);
            return parse(matcher, capturingBase, n-1, m, lastArgValue);
        } else {
            return parse(matcher, capturingBase+1, -2, memento, lastArgValue);
        }
    }
    
    protected abstract Object parse(Matcher matcher, int capturingBase, int width, Object memento, Object lastArgValue);
    
    public class WidthMatchSubpatterns extends PatternChoice {

        private final Justification j;
        private final char padding;
        private int paddingWidth = -1;
        
        public WidthMatchSubpatterns(PatternAPI api, int width, Justification j, char padding) {
            super(api, width+2);
            this.j = j;
            this.padding = padding;
        }

        @Override
        protected void beforeSubpattern() {
            if (paddingWidth < 0) return;
            if (j == Justification.Right) appendPadding(api(), padding, paddingWidth);
            if (j == Justification.Center) appendPadding(api(), padding, paddingWidth/2);
        }

        @Override
        protected void afterSubpattern() {
            if (paddingWidth < 0) return;
            if (j == Justification.Left) appendPadding(api(), padding, paddingWidth);
            if (j == Justification.Center) appendPadding(api(), padding, (paddingWidth+1)/2);
        }

    }

}
