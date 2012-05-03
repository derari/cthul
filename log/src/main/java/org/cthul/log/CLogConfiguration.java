package org.cthul.log;

import java.util.Locale;
import org.cthul.strings.format.FormatConfiguration;

/**
 *
 * @author Arian Treffer
 */
public class CLogConfiguration extends FormatConfiguration {
    
    private static final CLogConfiguration Default = new CLogConfiguration(FormatConfiguration.getDefault());

    public static CLogConfiguration getDefault() {
        return Default;
    }
    
    private final CLogConfiguration parent;

    public CLogConfiguration(CLogConfiguration parent) {
        super(parent);
        this.parent = parent;
    }
    
    public CLogConfiguration(FormatConfiguration parent) {
        super(parent);
        this.parent = null;
    }

    public CLogConfiguration() {
        this.parent = null;
    }
    
    @Override
    public CLogConfiguration forLocale(Locale locale) {
        CLogConfiguration c = new CLogConfiguration(this);
        c.setLocale(locale);
        return c;
    }
    
}
