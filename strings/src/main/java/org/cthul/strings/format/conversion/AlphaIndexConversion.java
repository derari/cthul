package org.cthul.strings.format.conversion;

import java.io.IOException;
import java.util.Locale;
import org.cthul.strings.AlphaIndex;
import org.cthul.strings.format.FormatterConfiguration;

/**
 * {@link AlphaIndex} format, lower case by default.
 * {@linkplain FormatAlignmentBase Width} is supported.
 * <p/>
 * The precision defines the base of the conversion (default 0),
 * the space flag allows that the highest value below the base is converted
 * into an empty string, instead of throwing an exception.
 * For instance, {@code "% .1Ia"} will produce {@code ""} for {@code 0}, 
 * and {@code "A"} for {@code 1}.
 * 
 * @author Arian Treffer
 */
public class AlphaIndexConversion extends FormatAlignmentBase {

    public static final AlphaIndexConversion INSTANCE = new AlphaIndexConversion();
    
    private static final char[] FLAGS = flags(F_JUSTIFICATION, F_PADDING, " ");
    private static final char[] F_SPACE = flags(" ");

    @Override
    protected char[] getValidFlags() {
        return FLAGS;
    }
    
    /**
     * Registers this format as {@code %ia} and {@code %jAlpha}.
     * @param conf 
     */
    public void register(FormatterConfiguration conf) {
        conf.setShortFormat('a', this);
        conf.setLongFormat("Alpha", this);
    }
    
    @Override
    protected int format(Appendable a, Object value, Locale locale, String flags, int precision, String formatString, int position) throws IOException {
        long n = cast(value, Number.class).longValue();
        if (precision == -1) {
            ensureNoInvalidFlags(flags, F_SPACE);
        } else {
            n -= precision;
            if (n == -1 && containsFlag(' ', flags)) return 0;
        }
        a.append(AlphaIndex.ToAlpha(n, false));
        return 0;
    }
    
}
