package org.cthul.strings.format.conversion;

import java.io.IOException;
import java.util.Locale;
import org.cthul.strings.Pluralizer;
import org.cthul.strings.format.FormatterConfiguration;
import org.cthul.strings.plural.PluralizerRegistry;

/**
 * {@linkplain Pluralizer Pluralizes} the value, using the given locale.
 * {@linkplain FormatAlignmentBase Width} is supported,
 * precision and other flags are not.
 * 
 * @author Arian Treffer
 */
public class PluralConversion extends FormatAlignmentBase {

    public static final PluralConversion INSTANCE = new PluralConversion();
    
    private final PluralizerRegistry reg;

    public PluralConversion(PluralizerRegistry reg) {
        this.reg = reg;
    }

    public PluralConversion() {
        this(PluralizerRegistry.INSTANCE);
    }

    /**
     * Registers this format as {@code %iP} and {@code %jPlural}.
     * @param conf 
     */
    public void register(FormatterConfiguration conf) {
        conf.setShortFormat('P', this);
        conf.setLongFormat("Plural", this);
    }
    
    @Override
    protected int format(Appendable a, Object value, Locale locale, String flags, int precision, String formatString, int position) throws IOException {
        ensureNoPrecision(precision);
        Pluralizer p = reg.find(locale);
        if (p == null) p = reg.find(Locale.getDefault());
        a.append(p.pluralOf(value.toString()));
        return 0;
    }
    
}
