package org.cthul.monad.util;

import org.cthul.monad.*;
import org.cthul.monad.adapt.ExceptionWrapper;

public abstract class ScopedExceptionType<X extends Exception>
            extends AbstractExceptionType<X>
            implements ExceptionWrapper<X> {

    public static <X extends Exception & Unsafe<?, ?>> ScopedExceptionType<X> build(ScopedFactory<X> factory) {
        X x = noValue(factory);
        return build(x.getScope(), (Class<X>) x.getClass(), factory);
    }

    public static <X extends Exception & Unsafe<?, ?>> ScopedExceptionType<X> build(Class<X> clazz, ScopedFactory<X> factory) {
        X x = noValue(factory);
        return build(x.getScope(), clazz, factory);
    }

    public static <X extends Exception> ScopedExceptionType<X> build(Scope scope, Factory<X> factory) {
        X x = noValue(scope, factory);
        return build(scope, (Class<X>) x.getClass(), factory);
    }

    public static <X extends Exception> ScopedExceptionType<X> build(Scope scope, Class<X> clazz, Factory<X> factory) {
        return new ScopedExceptionType<X>(clazz, scope, DefaultStatus.INTERNAL_ERROR) {
            @Override
            protected X newException(Scope scope, Status status, String message, Throwable cause) {
                return factory.newException(scope, status, message, cause);
            }
        };
    }

    public ScopedExceptionType(Class<X> exceptionClass, Scope scope, Status defaultStatus) {
        super(exceptionClass, scope, defaultStatus);
    }
}
