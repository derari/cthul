package org.cthul.strings.format;

import org.cthul.strings.Formatter;
import java.io.IOException;
import java.util.Locale;

/**
 *
 * @author Arian Treffer
 */
public abstract class FormatAlignmentBase extends FormatBase {

    protected static final String F_JUSTIFICATION = "-|";
    protected static final String F_PADDING = "_0";
    
    private static final char[] F_ALL = flags(F_JUSTIFICATION + F_PADDING);
    private static final char[] F_JUST = flags(F_JUSTIFICATION);
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
        char p = selectFlag(flags, F_PAD);
        if (p != 0) return p;
        return getDefaultPaddingChar();
    }
    
    protected static enum Justification {
        Left, Right, Center;
    }
    
    protected Justification getJustification(String flags) {
        char j = selectFlag(flags, F_JUST);
        switch (j) {
            case '-':
                return Justification.Left;
            case '|':
                return Justification.Center;
            default:
                return Justification.Right;
        }
    }
    
    @Override
    public int format(FormatterAPI formatter, Object value, Locale locale, String flags, int width, int precision, String formatString, int position) throws IOException {
        ensureValidFlags(flags, getValidFlags());
        if (width < 0) {
            return format(formatter, value, locale, flags, precision, formatString, position);
        } else {
            Justification j = getJustification(flags);
            char p = getPaddingChar(flags);
            StringBuilder sb = new StringBuilder();
            int result = format(sb, value, locale, flags, precision, formatString, position);
            switch (j) {
                case Left:
                    justifyLeft(formatter, sb, p, width);
                    break;
                case Center:
                    justifyCenter(formatter, sb, p, width);
                    break;
                default:
                    justifyRight(formatter, sb, p, width);
            }
            return result;
        }
    }
    
    protected abstract int format(Appendable a, Object value, Locale locale, String flags, int precision, String formatString, int position) throws IOException;
    
}
