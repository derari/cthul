package org.cthul.monad;

import org.cthul.monad.cache.CacheInfo;
import org.cthul.monad.error.ErrorState;
import org.cthul.monad.result.ExceptionValue;
import org.cthul.monad.result.NoResult;
import org.cthul.monad.util.ScopedRuntimeExceptionType;

public class ScopedRuntimeException extends RuntimeException implements NoResult, ExceptionValue.Delegator<RuntimeException> {

    public static Type withScope(Scope scope) {
        return new Type(scope);
    }

    private final ExceptionValue<?> value;

    public ScopedRuntimeException(Scope scope, Status status, String message) {
        super(message);
        this.value = ExceptionValue.of(scope, status, this);
    }

    public ScopedRuntimeException(Scope scope, Status status, String message, Throwable cause) {
        super(message, cause);
        this.value = ExceptionValue.of(scope, status, this);
    }

    public ScopedRuntimeException(Scope scope, Status status, Throwable cause) {
        super(cause);
        this.value = ExceptionValue.of(scope, status, this);
    }

    public ScopedRuntimeException(Scope scope, Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.value = ExceptionValue.of(scope, status, this);
    }

    public ScopedRuntimeException(ExceptionValue<?> exceptionValue) {
        super(exceptionValue.getMessage(), exceptionValue.getException());
        this.value = exceptionValue;
    }

    public ScopedRuntimeException cacheControl(CacheInfo meta) {
        value.setCacheControl(meta);
        return this;
    }

    @Override
    public ExceptionValue<?> getExceptionValue() {
        return value;
    }

    @Override
    public ScopedException asScopedException() {
        return value.asScopedException();
    }

    @Override
    public ScopedRuntimeException getException() {
        return this;
    }

    public ErrorState<?> getErrorState() {
        return value.getErrorState();
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
