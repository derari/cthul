package org.cthul.monad;

import org.cthul.monad.cache.CacheInfo;
import org.cthul.monad.error.ErrorState;
import org.cthul.monad.error.GeneralError;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ResultMessage;
import org.cthul.monad.util.ScopedExceptionType;

public class ScopedException extends Exception implements NoValue<ScopedException>, CacheInfo.Delegator {

    public static Type withScope(Scope scope) {
        return new Type(scope);
    }

    public static ScopedException parseMessage(ResultMessage resultMessage) {
        Status status = Status.withDescription(resultMessage.getCode(), resultMessage.getStatus());
        Scope adhocScope = resultMessage::getScope;
        return new ScopedException(adhocScope, status, resultMessage.getMessage());
    }

    private static final CacheInfo NO_STORE = CacheInfo.noStore();

    private final Scope scope;
    private final Status status;
    private ScopedRuntimeException runtimeException;
    private CacheInfo cachedMeta;
    private ErrorState<?> errorState;

    protected ScopedException(Scope scope, Status status, String message) {
        super(message);
        this.scope = scope;
        this.status = status;
        this.cachedMeta = NO_STORE;
    }

    protected ScopedException(Scope scope, Status status, String message, Throwable cause) {
        super(message, cause);
        this.scope = scope;
        this.status = status;
        this.cachedMeta = NO_STORE;
    }

    protected ScopedException(Scope scope, Status status, Throwable cause) {
        super(cause);
        this.scope = scope;
        this.status = status;
        this.cachedMeta = NO_STORE;
    }

    protected ScopedException(Scope scope, Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.scope = scope;
        this.status = status;
        this.cachedMeta = NO_STORE;
    }

    protected ScopedException(ScopedRuntimeException runtimeException) {
        super(runtimeException.getMessage(), runtimeException);
        this.runtimeException = runtimeException;
        this.scope = runtimeException.getScope();
        this.status = runtimeException.getStatus();
        this.cachedMeta = runtimeException.getCacheInfo();
    }

    @Override
    public CacheInfo getCacheInfo() {
        return cachedMeta;
    }

    public ScopedException cacheControl(CacheInfo meta) {
        this.cachedMeta = meta;
        return this;
    }

    public void setCacheControl(CacheInfo cachedMeta) {
        this.cachedMeta = cachedMeta;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public ScopedException getException() {
        return this;
    }

    public ScopedRuntimeException getRuntimeException() {
        if (runtimeException == null) {
            runtimeException = new ScopedRuntimeException(this);
        }
        return runtimeException;
    }

    @Override
    public ScopedRuntimeException unchecked() {
        return getRuntimeException();
    }

    public ErrorState<?> getErrorState() {
        if (errorState == null) {
            errorState = new GeneralError<>((Exception) this);
        }
        return errorState;
    }

    protected void setErrorState(ErrorState<?> errorState) {
        this.errorState = errorState;
    }

    @Override
    public String toString() {
        String s = getScope() + " " + getStatus();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
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
