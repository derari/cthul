package org.cthul.strings.format;

import java.io.IOException;
import org.cthul.strings.AlphaIndex;

/**
 * {@link AlphaIndex} format, lower case by default.
 * {@linkplain FormatAlignmentBase Width} is supported,
 * precision and other flags are not.
 * 
 * @author Arian Treffer
 */
public class AlphaIndexFormat extends FormatAlignmentBase {

    public static final AlphaIndexFormat INSTANCE = new AlphaIndexFormat();
    
    /**
     * Registers this format as {@code %ia} and {@code %jAlpha}.
     * @param conf 
     */
    public void register(FormatConfiguration conf) {
        conf.setShortFormat('a', this);
        conf.setLongFormat("Alpha", this);
    }
    
    @Override
    protected int format(Appendable a, Object value, java.util.Locale locale, String flags, int precision, String formatString, int position) throws IOException {
        ensureNoPrecision(precision);
        Number n = (Number) value;
        a.append(AlphaIndex.ToAlpha(n.longValue(), false));
        return 0;
    }
    
}
