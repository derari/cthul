package org.cthul.monad;

import org.cthul.monad.cache.CacheInfo;
import org.cthul.monad.error.ErrorState;
import org.cthul.monad.result.NoResult;
import org.cthul.monad.util.ScopedRuntimeExceptionType;

public class ScopedRuntimeException extends RuntimeException implements NoResult, CacheInfo.Delegator {

    public static Type withScope(Scope scope) {
        return new Type(scope);
    }

    private final Scope scope;
    private final Status status;
    private ScopedException checkedException;

    protected ScopedRuntimeException(Scope scope, Status status, String message) {
        super(message);
        this.scope = scope;
        this.status = status;
    }

    protected ScopedRuntimeException(Scope scope, Status status, String message, Throwable cause) {
        super(message, cause);
        this.scope = scope;
        this.status = status;
    }

    protected ScopedRuntimeException(Scope scope, Status status, Throwable cause) {
        super(cause);
        this.scope = scope;
        this.status = status;
    }

    protected ScopedRuntimeException(Scope scope, Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.scope = scope;
        this.status = status;
    }

    protected ScopedRuntimeException(ScopedException checkedException) {
        super(checkedException.getMessage(), checkedException);
        this.checkedException = checkedException;
        this.scope = checkedException.getScope();
        this.status = checkedException.getStatus();
    }

    @Override
    public CacheInfo getCacheInfo() {
        return getCheckedException();
    }

    public ScopedRuntimeException cacheControl(CacheInfo meta) {
        getCheckedException().setCacheControl(meta);
        return this;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    public ScopedException getCheckedException() {
        if (checkedException == null) {
            checkedException = new ScopedException(this);
        }
        return checkedException;
    }

    @Override
    public ScopedRuntimeException getException() {
        return this;
    }

    public ErrorState<?> getErrorState() {
        return getCheckedException().getErrorState();
    }

    protected void setErrorState(ErrorState<?> errorState) {
        getCheckedException().setErrorState(errorState);
    }

    public ScopedException checked() {
        return getCheckedException();
    }

    @Override
    public String toString() {
        String s = String.valueOf(getStatus());
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }

    public static class Type extends ScopedRuntimeExceptionType<ScopedRuntimeException> {

        public Type(Scope scope) {
            this(scope, DefaultStatus.INTERNAL_ERROR);
        }

        public Type(Scope scope, Status defaultStatus) {
            super(ScopedRuntimeException.class, scope, defaultStatus);
        }

        @Override
        protected ScopedRuntimeException newException(Scope scope, Status status, String message, Throwable cause) {
            return new ScopedRuntimeException(scope, status, message, cause);
        }
    }
}
