package org.cthul.log;

import java.util.Locale;
import org.cthul.strings.format.FormatterConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Arian Treffer
 */
public class CLogConfiguration extends FormatterConfiguration {
    
    private static final CLogConfiguration Default = new CLogConfiguration(FormatterConfiguration.getDefault());

    public static CLogConfiguration getDefault() {
        return Default;
    }
    
    protected final CLogConfiguration parent;

    public CLogConfiguration(CLogConfiguration parent) {
        super(parent);
        this.parent = parent;
    }
    
    public CLogConfiguration(FormatterConfiguration parent) {
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
    
    public CLogger getLogger(Logger logger) {
        return new CLogger(logger, this);
    }
    
    public CLogger getLogger(String name) {
        return new CLogger(slfLogger(name), this);
    }
    
    public CLogger getLogger(Class clazz) {
        return new CLogger(slfLogger(clazz), this);
    }
    
    /**
     * Chooses a logger based on the class name of the caller of this method.
     * Equivalent to {@link #getClassLogger(int) getClassLogger(0)}.
     * @param i
     * @return a logger
     */
    public CLogger getClassLogger() {
        return getLogger(detectClass(1));
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
        return getLogger(detectClass(i+1));
    }
    
    protected Logger slfLogger(Class clazz) {
        return LoggerFactory.getLogger(clazz);
    }
            
    protected Logger slfLogger(String name) {
        return LoggerFactory.getLogger(name);
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
