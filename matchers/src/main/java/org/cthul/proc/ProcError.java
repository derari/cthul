package org.cthul.proc;

import java.lang.reflect.InvocationTargetException;

/**
 * If thrown in a {@link ProcBase} implementation, the execution will fail.
 * A {@code ProcError} indicates internal errors, as opposed to errors that 
 * occur during the execution of the actual code.
 */
public class ProcError extends Error {

    private final REFactory reFactory;

    public ProcError(REFactory reFactory, String message) {
        super(message);
        this.reFactory = reFactory;
    }

    public ProcError(REFactory reFactory, Throwable cause) {
        super(cause);
        this.reFactory = reFactory;
    }

    public ProcError(REFactory reFactory, String message, Throwable cause) {
        super(message, cause);
        this.reFactory = reFactory;
    }
    
    public ProcError(Class<? extends RuntimeException> reClazz, String message) {
        this(reFactory(reClazz), message);
    }

    public ProcError(Class<? extends RuntimeException> reClazz, Throwable cause) {
        this(reFactory(reClazz), cause);
    }

    public ProcError(Class<? extends RuntimeException> reClazz, String message, Throwable cause) {
        this(reFactory(reClazz), message, cause);
    }
    
    public RuntimeException asRuntimeException() {
        return reFactory.create(getMessage(), getCause());
    }
    
    public static REFactory reFactory(Class<? extends RuntimeException> reClazz) {
        if (reClazz == RuntimeException.class) return RuntimeExceptionFactory;
        if (reClazz == IllegalArgumentException.class) return IllegalArgumentExceptionFactory;
        return new ReflectiveREFactory(reClazz);
    }
    
    public static final REFactory RuntimeExceptionFactory = new ReflectiveREFactory(RuntimeException.class);
    public static final REFactory IllegalArgumentExceptionFactory = new ReflectiveREFactory(IllegalArgumentException.class);
    
    public static interface REFactory {
        RuntimeException create(String message, Throwable cause);
    }
    
    public static class ReflectiveREFactory implements REFactory {
        
        private final Class<? extends RuntimeException> clazz;

        public ReflectiveREFactory(Class<? extends RuntimeException> clazz) {
            this.clazz = clazz;
        }

        @Override
        public RuntimeException create(String message, Throwable cause) {
            try {
                if (message == null && cause == null)
                    try {
                        return clazz.getConstructor().newInstance();
                    } catch (NoSuchMethodException ex) { }
                if (message == null)
                    try {
                        return clazz.getConstructor(Throwable.class).newInstance(cause);
                    } catch (NoSuchMethodException ex) { }
                if (cause == null)
                    try {
                        return clazz.getConstructor(String.class).newInstance(message);
                    } catch (NoSuchMethodException ex) { }
                try {
                    return clazz.getConstructor(String.class, Throwable.class).newInstance(message, cause);
                } catch (NoSuchMethodException ex) { }
                throw new RuntimeException("No suitable constructor in " + clazz);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e.getCause());
            } catch (InstantiationException | IllegalAccessException | SecurityException e) {
                throw new RuntimeException(e);
            }
        }
        
    }
    
}
