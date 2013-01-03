package org.cthul.log;

import java.util.Locale;
import org.cthul.strings.format.FormatterConfiguration;
import org.slf4j.Logger;

/**
 *
 * @author Arian Treffer
 */
public class CLogConfiguration extends CLogConfigurationBase {
    
    private static final CLogConfiguration Default = new CLogConfiguration(FormatterConfiguration.getDefault());

    public static CLogConfiguration getDefault() {
        return Default;
    }
    
    public CLogConfiguration(CLogConfigurationBase parent) {
        super(parent);
    }
    
    public CLogConfiguration(FormatterConfiguration parent) {
        super(parent);
    }

    public CLogConfiguration() {
        super();
    }

    @Override
    public CLogConfiguration newSubconfiguration() {
        return new CLogConfiguration(this);
    }

    @Override
    public CLogConfiguration forLocale(Locale locale) {
        return (CLogConfiguration) super.forLocale(locale);
    }
    
    public CLogger getLogger(Logger logger) {
        return new CLogger(logger, this);
    }
    
    public CLogger getLogger(String name) {
        return getLogger(slfLogger(name));
    }
    
    public CLogger getLogger(Class clazz) {
        return getLogger(slfLogger(clazz));
    }
    
    /**
     * Chooses a logger based on the class name of the caller of this method.
     * Equivalent to {@link #getClassLogger(int) getClassLogger(0)}.
     * @return a logger
     */
    public CLogger getClassLogger() {
        return getLogger(slfLogger(1));
    }
    
    /**
     * Chooses a logger based on the class name of the caller of this method.
     * {@code i == 0} identifies the caller of this method, for {@code i > 0}, 
     * the stack is walked upwards.
     * @param i
     * @return a logger
     */
    public CLogger getClassLogger(int i) {
        if (i < 0) {
            throw new IllegalArgumentException("Expected value >= 0, got " + i);
        }
        return getLogger(slfLogger(i+1));
    }
    
}
