package org.cthul.log.format;

import java.io.IOException;
import org.cthul.log.CLogConfiguration;

/**
 *
 * @author Arian Treffer
 */
public class ClassName extends FormatBase {

    public static final ClassName INSTANCE = new ClassName();
    
    public static void register(CLogConfiguration conf) {
        conf.setShortFormat('C', INSTANCE);
        conf.setLongFormat("Class", INSTANCE);
    }
    
    @Override
    public int format(Formatter f, Object value, String flags, int width, int precision, String input, int inputPosition) throws IOException {
        ensureNoFlags(flags);
        ensureNoWidth(width);
        ensureNoPrecision(precision);
        f.append(value == null ? "null" : value.getClass().getName());
        return 0;
    }
    
}
