package org.cthul.strings.format.conversion;

import java.io.IOException;
import java.util.Locale;
import org.cthul.strings.format.FormatterAPI;

/**
 * Provides justification (left, center, right) of output when a width is given.
 * By default, uses the following flags:
 * <pre>
 *   - -> left-justify output
 *   | -> center output
 *        (default: right-justified)
 *   _ -> padding with underscore
 *   0 -> padding with zeros
 *        (default: space)
 * </pre>
 * 
 * @author Arian Treffer
 */
public abstract class FormatAlignmentBase extends FormatConversionBase {

    private static final char[] F_ALL = flags(F_JUSTIFICATION, F_PADDING);
    private static final char[] F_PAD = flags(F_PADDING);
    
    protected char[] getValidFlags() {
        return F_ALL;
    }
    
    protected char[] getValidDuplicateFlags() {
        return NO_FLAGS;
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
    
    protected void validateFlags(String flags){
        ensureValidFlags(flags, getValidFlags());
        ensureNoDuplicatesExcept(flags, getValidDuplicateFlags());
    }
    
    @Override
    public int format(FormatterAPI formatter, Object value, Locale locale, String flags, int width, int precision, String formatString, int position) throws IOException {
        validateFlags(flags);
        if (width < 0) {
            ensureNoInvalidFlags(flags, getPaddingFlags());
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
