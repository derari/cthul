package org.cthul.strings.format;

import java.io.IOException;

/**
 * Prints the class name of the value.
 * {@linkplain FormatAlignmentBase Width} is supported,
 * precision and other flags are not.
 *
 * @author Arian Treffer
 */
public class ClassNameFormat extends FormatAlignmentBase {

    public static final ClassNameFormat INSTANCE = new ClassNameFormat();
    
    /**
     * Registers this format as {@code %iC} and {@code %jClass}.
     * @param conf 
     */
    public void register(FormatConfiguration conf) {
        conf.setShortFormat('C', this);
        conf.setLongFormat("Class", this);
    }
    
    private static final char[] FLAGS = flags(F_JUSTIFICATION + "_" );
    private static final char[] F_PAD = flags("_"); // pad with space or underscore

    @Override
    protected char[] getValidFlags() {
        return FLAGS;
    }

    @Override
    protected char[] getPaddingFlags() {
        return F_PAD;
    }

    @Override
    protected int format(Appendable a, Object value, java.util.Locale locale, String flags, int precision, String formatString, int position) throws IOException {
        ensureNoPrecision(precision);
        final String string = value == null ? "null" : value.getClass().getName();
        a.append(string);
        return 0;
    }
    
}
