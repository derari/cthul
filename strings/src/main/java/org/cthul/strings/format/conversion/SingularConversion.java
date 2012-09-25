package org.cthul.strings.format.conversion;

import java.io.IOException;
import java.util.Locale;
import org.cthul.strings.Pluralizer;
import org.cthul.strings.format.FormatterConfiguration;
import org.cthul.strings.plural.PluralizerRegistry;

/**
 * {@linkplain Pluralizer Singularizes} the value, using the given locale.
 * {@linkplain FormatAlignmentBase Width} is supported,
 * precision and other flags are not.
 * 
 * @author Arian Treffer
 */
public class SingularConversion extends FormatAlignmentBase {

    public static final SingularConversion INSTANCE = new SingularConversion();
    
    private final PluralizerRegistry reg;

    public SingularConversion(PluralizerRegistry reg) {
        this.reg = reg;
    }

    public SingularConversion() {
        this(PluralizerRegistry.INSTANCE);
    }

    /**
     * Registers this format as {@code %iS} and {@code %jSingular}.
     * @param conf 
     */
    public void register(FormatterConfiguration conf) {
        conf.setShortFormat('S', this);
        conf.setLongFormat("Singular", this);
    }
    
    @Override
    protected int format(Appendable a, Object value, Locale locale, String flags, int precision, String formatString, int position) throws IOException {
        ensureNoPrecision(precision);
        Pluralizer p = reg.find(locale);
        a.append(p.singularOf(value.toString()));
        return 0;
    }
    
}
