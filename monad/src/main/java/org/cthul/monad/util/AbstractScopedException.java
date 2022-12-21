package org.cthul.monad.util;

import org.cthul.monad.*;
import org.cthul.monad.cache.CacheInfo;
import org.cthul.monad.result.*;

public abstract class AbstractScopedException extends Exception {
    private ScopedRuntimeException runtimeException;
    private ExceptionValue<?> value;

    protected AbstractScopedException(Scope scope, Status status, String message) {
        super(message);
        if (scope == null) throw new NullPointerException("scope");
        this.value = ExceptionValue.of(scope, status, this);
    }

    protected AbstractScopedException(Scope scope, Status status, String message, Throwable cause) {
        super(message, cause);
        this.value = ExceptionValue.of(scope, status, this);
    }

    protected AbstractScopedException(Scope scope, Status status, Throwable cause) {
        super(cause);
        this.value = ExceptionValue.of(scope, status, this);
    }

    protected AbstractScopedException(Scope scope, Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.value = ExceptionValue.of(scope, status, this);
    }

    protected AbstractScopedException(ExceptionValue<?> exceptionValue) {
        super(exceptionValue.getMessage(), exceptionValue.getException());
        this.value = exceptionValue;
    }

    public ExceptionValue<?> getExceptionValue() {
        return value;
    }

    public Scope getScope() {
        return getExceptionValue().getScope();
    }

    public Status getStatus() {
        return getExceptionValue().getStatus();
    }

    public Exception getException() {
        return this;
    }

    public AbstractScopedException cacheControl(CacheInfo meta) {
        setCacheControl(meta);
        return this;
    }

    public void setCacheControl(CacheInfo cachedMeta) {
        getExceptionValue().setCacheControl(cachedMeta);
    }

    public ScopedRuntimeException asRuntimeException() {
        if (runtimeException != null) {
            return runtimeException;
        }
        Exception other = getExceptionValue().getException();
        if (other instanceof ScopedRuntimeException) {
            runtimeException = (ScopedRuntimeException) other;
        } else {
            runtimeException = newScopedRuntimeException();
        }
        return runtimeException;
    }

    protected ScopedRuntimeException newScopedRuntimeException() {
        if (this instanceof ExceptionValue) {
            return new ScopedRuntimeException((ExceptionValue<?>) this);
        }
        return new ScopedRuntimeException(getExceptionValue());
    }

    public ScopedRuntimeException unchecked() {
        return asRuntimeException();
    }

    @Override
    public String toString() {
        String s = getScope() + " " + getStatus();
        String message = getLocalizedMessage();
        return (message != null) ? (s + ": " + message) : s;
    }
}
