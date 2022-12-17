package org.cthul.monad.util;

import org.cthul.monad.*;
import org.cthul.monad.cache.CacheInfo;
import org.cthul.monad.error.ErrorState;
import org.cthul.monad.error.GeneralError;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ResultMessage;

public abstract class AbstractScopedException extends Exception implements CacheInfo.Delegator {

    private static final CacheInfo NO_STORE = CacheInfo.noStore();

    private final Scope scope;
    private final Status status;
    private ScopedRuntimeException runtimeException;
    private CacheInfo cachedMeta;
    private ErrorState<?> errorState;

    protected AbstractScopedException(Scope scope, Status status, String message) {
        super(message);
        if (scope == null) throw new NullPointerException("scope");
        if (status == null) throw new NullPointerException("status");
        this.scope = scope;
        this.status = status;
        this.cachedMeta = NO_STORE;
    }

    protected AbstractScopedException(Scope scope, Status status, String message, Throwable cause) {
        super(message, cause);
        if (scope == null) throw new NullPointerException("scope");
        if (status == null) throw new NullPointerException("status");
        this.scope = scope;
        this.status = status;
        this.cachedMeta = NO_STORE;
    }

    protected AbstractScopedException(Scope scope, Status status, Throwable cause) {
        super(cause);
        if (scope == null) throw new NullPointerException("scope");
        if (status == null) throw new NullPointerException("status");
        this.scope = scope;
        this.status = status;
        this.cachedMeta = NO_STORE;
    }

    protected AbstractScopedException(Scope scope, Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        if (scope == null) throw new NullPointerException("scope");
        if (status == null) throw new NullPointerException("status");
        this.scope = scope;
        this.status = status;
        this.cachedMeta = NO_STORE;
    }

    protected AbstractScopedException(ScopedRuntimeException runtimeException) {
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

    public AbstractScopedException cacheControl(CacheInfo meta) {
        this.cachedMeta = meta;
        return this;
    }

    public void setCacheControl(CacheInfo cachedMeta) {
        this.cachedMeta = cachedMeta;
    }

    public Scope getScope() {
        return scope;
    }

    public Status getStatus() {
        return status;
    }

    public abstract AbstractScopedException getException();

    public ScopedRuntimeException getRuntimeException() {
        if (runtimeException == null) {
            runtimeException = asScopedRuntimeException();
        }
        return runtimeException;
    }

    protected abstract ScopedRuntimeException asScopedRuntimeException();

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
}
