package org.cthul.monad;

import java.util.function.Function;
import org.cthul.monad.cache.CachedMeta;
import org.cthul.monad.error.ErrorState;

public class ScopedRuntimeException extends RuntimeException implements Result.Unchecked<Object>, CachedMeta.Delegator {
    
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
    public CachedMeta getCachedMeta() {
        return getCheckedException();
    }
    
    public ScopedRuntimeException cacheControl(CachedMeta meta) {
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

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public Object get() {
        throw getException();
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

    @Override
    public <U> U reduce(Function<? super Object, ? extends U> map, Function<? super ScopedRuntimeException, ? extends U> onError) {
        return onError.apply(this);
    }
    
    @Override
    public ScopedException checked() {
        return getCheckedException();
    }
    
    @Override
    public String toString() {
        String s = String.valueOf(getStatus());
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
