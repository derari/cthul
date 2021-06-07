package org.cthul.monad.util;

import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.function.ExceptionWrapper;
import org.cthul.monad.result.NoValue;

public abstract class ScopedExceptionType<X extends Exception> 
            extends AbstractExceptionType<X>
            implements ExceptionWrapper<X> {

    public ScopedExceptionType(Class<X> exceptionClass, Scope scope, Status defaultStatus) {
        super(exceptionClass, scope, defaultStatus);
    }
    
    @Override
    public NoValue<X> failed(Exception exception) {
        return wrap(exception);
    }
}
