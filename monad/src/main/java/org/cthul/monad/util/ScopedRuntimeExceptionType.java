package org.cthul.monad.util;

import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.adapt.ExceptionWrapper;
import org.cthul.monad.adapt.RuntimeExceptionWrapper;

public abstract class ScopedRuntimeExceptionType<X extends RuntimeException>
            extends AbstractExceptionType<X>
            implements RuntimeExceptionWrapper<X> {

    public ScopedRuntimeExceptionType(Class<X> exceptionClass, Scope scope, Status defaultStatus) {
        super(exceptionClass, scope, defaultStatus);
    }

    @Override
    protected ExceptionWrapper.ResultFactory<RuntimeException> unchecked(ResultFactory resultFactory) {
        return (ExceptionWrapper.ResultFactory) resultFactory;
    }
}
