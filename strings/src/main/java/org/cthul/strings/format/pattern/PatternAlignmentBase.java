package org.cthul.strings.format.pattern;

import java.util.Locale;
import org.cthul.strings.format.PatternAPI;

/**
 *
 * @author Arian Treffer
 */
public abstract class PatternAlignmentBase extends FormatPatternBase {
    
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
        pattern.append(padding);
        pattern.append("]*");
    }

    @Override
    public int toRegex(PatternAPI pattern, Locale locale, String flags, int width, int precision, String formatString, int position) {
        ensureValidFlags(flags, getValidFlags());
        Justification j = getJustification(flags, width);
        char padding = j == Justification.None ? 0 : getPaddingChar(flags);
        switch (j) {
            case Center:
            case Right:
                appendPadding(pattern, padding);
        }
        int result = toRegex(pattern, locale, flags, padding, precision, formatString, position);
        switch (j) {
            case Left:
            case Center:
                appendPadding(pattern, padding);
        }
        return result;
    }
    
    protected abstract int toRegex(PatternAPI regex, Locale locale, String flags, char padding, int precision, String formatString, int position);

}
