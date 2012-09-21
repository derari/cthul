package org.cthul.strings.format.conversion;

import java.io.IOException;
import org.cthul.strings.AlphaIndex;
import org.cthul.strings.format.FormatterConfiguration;

/**
 * {@link AlphaIndex} format, lower case by default.
 * {@linkplain FormatAlignmentBase Width} is supported,
 * precision and other flags are not.
 * 
 * @author Arian Treffer
 */
public class AlphaIndexConversion extends FormatAlignmentBase {

    public static final AlphaIndexConversion INSTANCE = new AlphaIndexConversion();
    
    /**
     * Registers this format as {@code %ia} and {@code %jAlpha}.
     * @param conf 
     */
    public void register(FormatterConfiguration conf) {
        conf.setShortFormat('a', this);
        conf.setLongFormat("Alpha", this);
    }
    
    @Override
    protected int format(Appendable a, Object value, java.util.Locale locale, String flags, int precision, String formatString, int position) throws IOException {
        ensureNoPrecision(precision);
        long n = cast(value, Number.class).longValue();
        a.append(AlphaIndex.ToAlpha(n, false));
        return 0;
    }
    
}
