package org.cthul.monad;

import org.cthul.monad.cache.CacheInfo;
import org.cthul.monad.error.ErrorState;
import org.cthul.monad.error.GeneralError;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ResultMessage;
import org.cthul.monad.util.AbstractScopedException;
import org.cthul.monad.util.ScopedExceptionType;

public class ScopedException extends AbstractScopedException implements NoValue<ScopedException> {

    public static Type withScope(Scope scope) {
        return new Type(scope);
    }

    public static ScopedException parseMessage(ResultMessage resultMessage) {
        Status status = Status.withDescription(resultMessage.getCode(), resultMessage.getStatus());
        Scope adhocScope = resultMessage::getScope;
        return new ScopedException(adhocScope, status, resultMessage.getMessage());
    }

    public ScopedException(Scope scope, Status status, String message) {
        super(scope, status, message);
    }

    public ScopedException(Scope scope, Status status, String message, Throwable cause) {
        super(scope, status, message, cause);
    }

    public ScopedException(Scope scope, Status status, Throwable cause) {
        super(scope, status, cause);
    }

    public ScopedException(Scope scope, Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(scope, status, message, cause, enableSuppression, writableStackTrace);
    }

    public ScopedException(ScopedRuntimeException runtimeException) {
        super(runtimeException);
    }

    @Override
    protected ScopedRuntimeException asScopedRuntimeException() {
        return new ScopedRuntimeException(this);
    }

    @Override
    public ScopedException getException() {
        return this;
    }

    @Override
    protected void setErrorState(ErrorState<?> errorState) {
        super.setErrorState(errorState);
    }

    public static class Type extends ScopedExceptionType<ScopedException> {

        public Type(Scope scope) {
            this(scope, DefaultStatus.INTERNAL_ERROR);
        }

        public Type(Scope scope, Status defaultStatus) {
            super(ScopedException.class, scope, defaultStatus);
        }

        @Override
        protected ScopedException newException(Scope scope, Status status, String message, Throwable cause) {
            return new ScopedException(scope, status, message, cause);
        }
    }
}
