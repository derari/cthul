package org.cthul.strings.format.conversion;

import java.io.IOException;
import java.util.Locale;
import org.cthul.strings.format.FormatException;
import org.cthul.strings.format.FormatterAPI;

/**
 * Throws a {@link FormatException} if invoked, can be used to explicitly clear
 * format entries in the registry.
 * 
 * @author Arian Treffer
 */
public class NullFormatConversion extends FormatConversionBase {
    
    public static final NullFormatConversion INSTANCE = new NullFormatConversion();

    @Override
    public int format(FormatterAPI f, Object value, Locale locale, String flags, int width, int precision, String input, int inputPosition) throws IOException {
        int i = input.lastIndexOf('%', inputPosition);
        if (i < 0) {
            throw FormatException.unknownFormat("");
        } else {
            throw FormatException.unknownFormat(input.substring(i, inputPosition));
        }
    }
    
}
