package org.cthul.log;

/**
 * Creates loggers using the {@link CLogConfiguration#getDefault() default} 
 * configuration.
 * @author Arian Treffer
 */
public class CLoggerFactory {
    
    public static CLogger getLogger(Class<?> clazz) {
        return CLogConfiguration.getDefault().getLogger(clazz);
    }
    
    public static CLogger getLogger(String name) {
        return CLogConfiguration.getDefault().getLogger(name);
    }
    
    public static CLogger getClassLogger() {
        return CLogConfiguration.getDefault().getClassLogger(1);
    }
    
}
