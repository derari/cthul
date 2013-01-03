package org.cthul.strings;

import org.cthul.strings.format.FormatterConfiguration;

/**
 * Helper for creating formatted exceptions.
 */
public class Exceptions {
    
    public static <T> T create(FormatterConfiguration c, Factory<T> f, String msg, Object... args) {
        String m = msg(c, msg, args);
        Throwable t = t(args);
        return f.create(m, t);
    }

    protected static String msg(FormatterConfiguration c, String msg, Object[] args) {
        String m = Strings.format(c, msg, args);
        return m;
    }

    protected static Throwable t(Object[] args) {
        Throwable t = args.length > 0 && args[0] instanceof Throwable ? (Throwable) args[0] : null;
        return t;
    }
    
    public static interface Factory<T> {
        T create(String message, Throwable cause);
    }
    
    public static final Factory<RuntimeException> RUNTIME_EXCEPTION = new Factory<RuntimeException>() {
        @Override
        public RuntimeException create(String message, Throwable cause) {
            return new RuntimeException(message, cause);
        }
    };
    
    public static final Factory<IllegalArgumentException> ILLEGAL_ARGUMENT = new Factory<IllegalArgumentException>() {
        @Override
        public IllegalArgumentException create(String message, Throwable cause) {
            return new IllegalArgumentException(message, cause);
        }
    };
    
    public static final Factory<IllegalStateException> ILLEGAL_STATE = new Factory<IllegalStateException>() {
        @Override
        public IllegalStateException create(String message, Throwable cause) {
            return new IllegalStateException(message, cause);
        }
    };
    
    public static final Factory<UnsupportedOperationException> UNSUPPORTED_OPERATION = new Factory<UnsupportedOperationException>() {
        @Override
        public UnsupportedOperationException create(String message, Throwable cause) {
            return new UnsupportedOperationException(message, cause);
        }
    };
    
}
