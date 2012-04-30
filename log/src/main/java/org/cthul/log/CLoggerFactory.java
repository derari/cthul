package org.cthul.log;

import org.slf4j.LoggerFactory;

/**
 *
 * @author Arian Treffer
 */
public class CLoggerFactory {
    
    public static CLogger getLogger(Class<?> clazz) {
        return new CLogger(LoggerFactory.getLogger(clazz));
    }
    
    public static CLogger getLogger(String name) {
        return new CLogger(LoggerFactory.getLogger(name));
    }
    
    public static CLogger getClasslogger() {
        return new CLogger(LoggerFactory.getLogger(detectClass(1)));
    }
    
    private static String detectClass(int i) {
        if (i < 1) {
            throw new IllegalArgumentException("Expected value > 0, got " + i);
        }
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        // 0 getStackTrace
        // 1 detectClass
        // 2 caller
        return stack[i+2].getClassName();
    }
    
}
