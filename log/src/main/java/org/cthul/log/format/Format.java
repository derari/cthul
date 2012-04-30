package org.cthul.log.format;

import java.io.IOException;

/**
 *
 * @author Arian Treffer
 */
public interface Format {
    
    public int format(Formatter f, Object value, String flags, int width, int precision, String input, int inputPosition) throws IOException;
    
}
