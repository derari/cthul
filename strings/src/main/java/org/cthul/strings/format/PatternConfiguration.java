package org.cthul.strings.format;

import java.util.Locale;
import org.cthul.strings.format.pattern.RegexPattern;

/**
 *
 * @author Arian Treffer
 */
public class PatternConfiguration extends AbstractFormatConfiguration<ConversionPattern> {

    private static final PatternConfiguration Default = new PatternConfiguration(null);

    public static PatternConfiguration getDefault() {
        return Default;
    }
    
    static {
        RegexPattern.INSTANCE.register(Default);
    }
    
    public PatternConfiguration() {
        this(getDefault());
    }

    public PatternConfiguration(PatternConfiguration parent) {
        super(parent, ConversionPattern.class);
    }

    @Override
    public PatternConfiguration forLocale(Locale locale) {
        return (PatternConfiguration) super.forLocale(locale);
    }

    @Override
    protected PatternConfiguration createChildConfig() {
        return new PatternConfiguration(this);
    }

    @Override
    protected ConversionPattern nullFormat() {
        throw new UnsupportedOperationException();
    }
    
}
