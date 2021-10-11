package org.cthul.monad.util;

import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.adapt.ExceptionWrapper;

public abstract class ScopedExceptionType<X extends Exception>
            extends AbstractExceptionType<X>
            implements ExceptionWrapper<X> {

    public ScopedExceptionType(Class<X> exceptionClass, Scope scope, Status defaultStatus) {
        super(exceptionClass, scope, defaultStatus);
    }
}
