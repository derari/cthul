package org.cthul.log.format;

import java.io.IOException;

/**
 *
 * @author Arian Treffer
 */
public class NullFormat extends FormatBase {
    
    public static final NullFormat INSTANCE = new NullFormat();

    @Override
    public int format(Formatter f, Object value, String flags, int width, int precision, String input, int inputPosition) throws IOException {
        int i = input.lastIndexOf('%', inputPosition);
        if (i < 0) {
            throw new FormatException("Unknown format");
        } else {
            throw new FormatException("Unknown format " + input.substring(i, inputPosition));
        }
    }
    
}
