package org.cthul.monad.util;

import org.cthul.monad.*;

public abstract class CustomScopedException extends AbstractScopedException {

    public CustomScopedException(Scope scope, Status status, String message) {
        super(scope, status, message);
    }

    public CustomScopedException(Scope scope, Status status, String message, Throwable cause) {
        super(scope, status, message, cause);
    }

    public CustomScopedException(Scope scope, Status status, Throwable cause) {
        super(scope, status, cause);
    }

    public CustomScopedException(Scope scope, Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(scope, status, message, cause, enableSuppression, writableStackTrace);
    }

    public CustomScopedException(ScopedRuntimeException runtimeException) {
        super(runtimeException);
    }

    @Override
    protected ScopedRuntimeException asScopedRuntimeException() {
        return new ScopedException(getScope(), getStatus(), getMessage(), this).unchecked();
    }
}
