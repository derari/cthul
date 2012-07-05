package org.cthul.strings.format;

import java.io.IOException;
import org.cthul.strings.Pluralizer;
import org.cthul.strings.plural.PluralizerRegistry;

/**
 * {@linkplain Pluralizer Singularizes} the value, using the given locale.
 * {@linkplain FormatAlignmentBase Width} is supported,
 * precision and other flags are not.
 * 
 * @author Arian Treffer
 */
public class SingularFormat extends FormatAlignmentBase {

    public static final SingularFormat INSTANCE = new SingularFormat();
    
    private static final char[] FLAGS = flags(F_JUSTIFICATION + "_" );
    private static final char[] F_PAD = flags("_"); // pad with space or underscore
    
    private final PluralizerRegistry reg;

    public SingularFormat(PluralizerRegistry reg) {
        this.reg = reg;
    }

    public SingularFormat() {
        this(PluralizerRegistry.INSTANCE);
    }

    /**
     * Registers this format as {@code %iS} and {@code %jSingular}.
     * @param conf 
     */
    public void register(FormatConfiguration conf) {
        conf.setShortFormat('S', this);
        conf.setLongFormat("Singular", this);
    }
    
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
        Pluralizer p = reg.get(locale);
        a.append(p.singularize(String.valueOf(value)));
        return 0;
    }
    
}
