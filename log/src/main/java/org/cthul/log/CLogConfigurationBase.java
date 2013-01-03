package org.cthul.log;

import java.util.Locale;
import org.cthul.strings.format.FormatterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LocationAwareLogger;

/**
 *
 * @author Arian Treffer
 */
public abstract class CLogConfigurationBase extends FormatterConfiguration {
    
    protected final CLogConfigurationBase parent;

    public CLogConfigurationBase(CLogConfigurationBase parent) {
        super(parent);
        this.parent = parent;
    }
    
    public CLogConfigurationBase(FormatterConfiguration parent) {
        super(parent);
        this.parent = null;
    }

    public CLogConfigurationBase() {
        this(CLogConfiguration.getDefault());
    }

    @Override
    public abstract CLogConfigurationBase newSubconfiguration();

    @Override
    public CLogConfigurationBase forLocale(Locale locale) {
        return (CLogConfigurationBase) super.forLocale(locale);
    }
    
    protected Logger slfLogger(Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }
            
    protected Logger slfLogger(String name) {
        return LoggerFactory.getLogger(name);
    }
    
    protected Logger slfLogger(int i) {
        return slfLogger(detectClass(i+1));
    }

    /**
     * {@code i == 0} identifies the caller of this method, for {@code i > 0}, 
     * the stack is walked upwards.
     * @param i
     * @return class name
     */
    protected static String detectClass(int i) {
        if (i < 1) {
            throw new IllegalArgumentException("Expected value > 0, got " + i);
        }
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // 0 getStackTrace
        // 1 detectClass
        // 2 getLogger
        // 3 caller
        if (stack.length <= i+2) return "";
        return stack[i+2].getClassName();
    }
    
}
