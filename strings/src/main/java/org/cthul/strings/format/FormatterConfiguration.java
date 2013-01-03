package org.cthul.strings.format;

import java.util.Locale;
import org.cthul.strings.format.conversion.*;

/**
 * Manages short and long formats.
 * The {@link #getDefault() default} configuration has all formats of the 
 * {@code conversion} package registered.
 * 
 * @author Arian Treffer
 */
public class FormatterConfiguration extends AbstractFormatConfiguration<FormatConversion> {
    
    private static final FormatterConfiguration DEFAULT = new FormatterConfiguration(null);

    public static FormatterConfiguration getDefault() {
        return DEFAULT;
    }
    
    static {
        AlphaIndexConversion.INSTANCE.register(DEFAULT);
        ClassNameConversion.INSTANCE.register(DEFAULT);
        ConditionalConversion.INSTANCE.register(DEFAULT);
        PluralConversion.INSTANCE.register(DEFAULT);
        RomansConversion.INSTANCE.register(DEFAULT);
        SingularConversion.INSTANCE.register(DEFAULT);
    }

    public FormatterConfiguration() {
        this(getDefault());
    }

    public FormatterConfiguration(FormatterConfiguration parent) {
        super(parent, FormatConversion.class);
    }

    @Override
    public FormatterConfiguration forLocale(Locale locale) {
        return (FormatterConfiguration) super.forLocale(locale);
    }
    
    @Override
    public FormatterConfiguration newSubconfiguration() {
        return new FormatterConfiguration(this);
    }

    @Override
    protected FormatConversion nullFormat() {
        return NullFormatConversion.INSTANCE;
    }
}
