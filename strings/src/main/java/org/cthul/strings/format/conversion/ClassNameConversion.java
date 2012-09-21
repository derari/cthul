package org.cthul.strings.format.conversion;

import java.io.IOException;
import org.cthul.strings.format.FormatterConfiguration;

/**
 * Prints the class name of the value.
 * {@linkplain FormatAlignmentBase Width} is supported,
 * precision and other flags are not.
 *
 * @author Arian Treffer
 */
public class ClassNameConversion extends FormatAlignmentBase {

    public static final ClassNameConversion INSTANCE = new ClassNameConversion();
    
    /**
     * Registers this format as {@code %iC} and {@code %jClass}.
     * @param conf 
     */
    public void register(FormatterConfiguration conf) {
        conf.setShortFormat('C', this);
        conf.setLongFormat("Class", this);
    }
    
    @Override
    protected int format(Appendable a, Object value, java.util.Locale locale, String flags, int precision, String formatString, int position) throws IOException {
        ensureNoPrecision(precision);
        final String string = value == null ? "null" : value.getClass().getName();
        a.append(string);
        return 0;
    }
    
}
