package org.cthul.log;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.slf4j.Logger;

/**
 *
 * @author Arian Treffer
 */
class CLoggerFix {

    private static boolean fixed = false;
    
    /**
     * JDK14 Logger uses a static field "SELF" to filter the stacktrace.
     * 
     * @param logger 
     */
    static synchronized void fix(Logger logger) {
        if (fixed || logger == null) return;
        fixed = true;
        Throwable t = null;
        Class<?> clazz = logger.getClass();
        try {
            Field fSelf = clazz.getDeclaredField("SELF");
            if (fSelf.getType().equals(String.class) &&
                    (fSelf.getModifiers() & Modifier.STATIC) != 0) {
                fSelf.setAccessible(true);
                fSelf.set(null, CLogger.class.getName());
            }
        } catch (NoSuchFieldException e) {
            // nothing to fix
        } catch (IllegalAccessException e) {
            t = e;
        } catch (SecurityException e) {
            t = e;
        }
        if (t != null) {
            logger.warn("Could not fix " + logger, t);
        }
    }
    
}
