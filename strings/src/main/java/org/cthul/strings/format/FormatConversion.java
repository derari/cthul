package org.cthul.strings.format;

import java.io.IOException;
import java.util.Locale;
import org.cthul.strings.format.conversion.FormatConversionBase;

/**
 *
 * @author Arian Treffer
 */
public interface FormatConversion {
    
    /**
     * Appends a string representation of {@code value} to the {@code formatter}.
     * Implementations should extend {@link FormatConversionBase}
     * @param formatter
     * @param value
     * @param flags flags parameter, non-empty string or {@code null}
     * @param width width parameter, or -1
     * @param precision precision parameter, or -1
     * @param formatString the original format string
     * @param position position in format string after this format call
     * @return additional characters that were read from {@code formatString},
     *         should be zero for most implementations.
     * @throws IOException 
     */
    public int format(FormatterAPI formatter, Object value, Locale locale, String flags, int width, int precision, String formatString, int position) throws IOException;
    
}
