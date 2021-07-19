package org.cthul.monad.util;

import org.cthul.monad.Unsafe;
import org.cthul.monad.result.NoValue;

public abstract class AbstractExceptionWrapper<X extends Exception> {

    protected NoValue<X> wrap(Exception exception) {
        if (exception instanceof Unsafe) {
            Unsafe<?, ?> unsafe = (Unsafe) exception;
            if (isWrapperFor(unsafe, exception)) {
                return wrapUnsafe(unsafe.noValue());
            }
        }
        return wrapException(exception);
    }

    protected boolean isWrapperFor(Unsafe<?, ?> unsafe, Exception exception) {
        if (!unsafe.isPresent()) return false;
        Exception wrapped = unsafe.getException();
        if (wrapped == exception) return true;
        return exception.getClass().isInstance(wrapped);
    }

    protected abstract NoValue<X> wrapException(Exception exception);

    protected abstract NoValue<X> wrapUnsafe(NoValue<?> unsafe);
}
