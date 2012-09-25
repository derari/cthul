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
    
    private static final FormatterConfiguration Default = new FormatterConfiguration(null);

    public static FormatterConfiguration getDefault() {
        return Default;
    }
    
    static {
        AlphaIndexConversion.INSTANCE.register(Default);
        ClassNameConversion.INSTANCE.register(Default);
        ConditionalConversion.INSTANCE.register(Default);
        PluralConversion.INSTANCE.register(Default);
        RomansConversion.INSTANCE.register(Default);
        SingularConversion.INSTANCE.register(Default);
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
    protected AbstractFormatConfiguration createChildConfig() {
        return new FormatterConfiguration(this);
    }

    @Override
    protected FormatConversion nullFormat() {
        return NullFormatConversion.INSTANCE;
    }
}
