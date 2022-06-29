package org.cthul.monad.util;

import org.cthul.monad.*;
import org.cthul.monad.adapt.ExceptionWrapper;
import org.cthul.monad.adapt.RuntimeExceptionWrapper;

public abstract class ScopedRuntimeExceptionType<X extends RuntimeException>
            extends AbstractExceptionType<X>
            implements RuntimeExceptionWrapper<X> {

    public static <X extends RuntimeException & Unsafe<?, ?>> ScopedRuntimeExceptionType<X> build(ScopedFactory<X> factory) {
        X x = noValue(factory);
        return build(x.getScope(), (Class<X>) x.getClass(), factory);
    }

    public static <X extends RuntimeException & Unsafe<?,?>> ScopedRuntimeExceptionType<X> build(Class<X> clazz, ScopedFactory<X> factory) {
        X x = noValue(factory);
        return build(x.getScope(), clazz, factory);
    }

    public static <X extends RuntimeException> ScopedRuntimeExceptionType<X> build(Scope scope, Factory<X> factory) {
        X x = noValue(scope, factory);
        return build(scope, (Class<X>) x.getClass(), factory);
    }

    public static <X extends RuntimeException> ScopedRuntimeExceptionType<X> build(Scope scope, Class<X> clazz, Factory<X> factory) {
        return new ScopedRuntimeExceptionType<X>(clazz, scope, DefaultStatus.INTERNAL_ERROR) {
            @Override
            protected X newException(Scope scope, Status status, String message, Throwable cause) {
                return factory.newException(scope, status, message, cause);
            }
        };
    }

    public ScopedRuntimeExceptionType(Class<X> exceptionClass, Scope scope, Status defaultStatus) {
        super(exceptionClass, scope, defaultStatus);
    }

    @Override
    protected ExceptionWrapper.ResultFactory<RuntimeException> unchecked(ResultFactory resultFactory) {
        return (ExceptionWrapper.ResultFactory) resultFactory;
    }
}
