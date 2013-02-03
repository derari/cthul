package org.cthul.strings.format.conversion;

import java.io.IOException;
import org.cthul.strings.format.FormatException;
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
        if (precision > 2) {
            throw FormatException.precisionTooHigh(getFormatName(), precision, 2);
        }
        final String string;
        if (value == null) {
            string = "null";
        } else {
            switch (precision) {
                case 0:
                    string = value.getClass().getSimpleName();
                    break;
                case 2:
                    string = value.getClass().getCanonicalName();
                    break;
                default:
                    string = value.getClass().getName();
                    break;
            }
        }
        a.append(string);
        return 0;
    }
    
}
