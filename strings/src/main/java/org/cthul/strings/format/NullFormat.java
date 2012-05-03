package org.cthul.strings.format;

import java.io.IOException;
import java.util.Locale;

/**
 * Throws a {@link FormatException} if invoked, can be used to explicitly clear
 * format entries in the registry.
 * 
 * @author Arian Treffer
 */
public class NullFormat extends FormatBase {
    
    public static final NullFormat INSTANCE = new NullFormat();

    @Override
    public int format(FormatterAPI f, Object value, Locale locale, String flags, int width, int precision, String input, int inputPosition) throws IOException {
        int i = input.lastIndexOf('%', inputPosition);
        if (i < 0) {
            throw new FormatException("Unknown format");
        } else {
            throw new FormatException("Unknown format " + input.substring(i, inputPosition));
        }
    }
    
}
