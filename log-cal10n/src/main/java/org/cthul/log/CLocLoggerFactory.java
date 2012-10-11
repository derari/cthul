package org.cthul.log;

/**
 *
 * @author Arian Treffer
 */
public class CLocLoggerFactory {
    
    public static <E extends Enum<?>> CLocLogger<E> getLogger(Class<?> clazz) {
        return CLocLogConfiguration.getDefault().getLogger(clazz);
    }
    
    public static <E extends Enum<?>> CLocLogger<E> getLogger(String name) {
        return CLocLogConfiguration.getDefault().getLogger(name);
    }
    
    public static <E extends Enum<?>> CLocLogger<E> getClassLogger() {
        return CLocLogConfiguration.getDefault().getClassLogger(1);
    }
    
}
