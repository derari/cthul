package org.cthul.monad.util;

import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.function.RuntimeExceptionWrapper;
import org.cthul.monad.result.NoResult;

public abstract class ScopedRuntimeExceptionType<X extends RuntimeException> 
            extends AbstractExceptionType<X>
            implements RuntimeExceptionWrapper<X> {

    public ScopedRuntimeExceptionType(Class<X> exceptionClass, Scope scope, Status defaultStatus) {
        super(exceptionClass, scope, defaultStatus);
    }
    
    @Override
    public NoResult failed(Exception exception) {
        return wrap(exception).unchecked();
    }
}
