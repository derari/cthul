package org.cthul.monad;

import java.util.function.Function;
import org.cthul.monad.cache.Cached;
import org.cthul.monad.cache.CachedMeta;
import org.cthul.monad.error.ErrorState;
import org.cthul.monad.error.GeneralError;

public class ScopedException extends Exception implements Cached<Object>, CachedMeta.Delegator {
    
    private static final CachedMeta NO_STORE = CachedMeta.noStore();
    
    private final Scope scope;
    private final Status status;
    private ScopedRuntimeException runtimeException;
    private CachedMeta cachedMeta;
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
        this.cachedMeta = runtimeException.getCachedMeta();
    }

    @Override
    public CachedMeta getCachedMeta() {
        return cachedMeta;
    }
    
    @Override
    public ScopedException cacheControl(CachedMeta meta) {
        this.cachedMeta = meta;
        return this;
    }

    public void setCacheControl(CachedMeta cachedMeta) {
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
    public boolean hasValue() {
        return false;
    }

    @Override
    public Object get() throws ScopedException {
        throw getException();
    }

    @Override
    public ScopedException getException() {
        return this;
    }

    @Override
    public <U> U reduce(Function<? super Object, ? extends U> map, Function<? super ScopedException, ? extends U> onError) {
        return onError.apply(this);
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
}
