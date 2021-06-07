package org.cthul.monad;

import org.cthul.monad.result.NoResult;

public class ValueIsPresentException extends RuntimeException implements NoResult {
    
    private final Result<?> value;

    public ValueIsPresentException(Result<?> value) {
        this.value = value;
    }

    public ValueIsPresentException(Result<?> value, String message) {
        super(message);
        this.value = value;
    }

    public ValueIsPresentException(Result<?> value, String message, Throwable cause) {
        super(message, cause);
        this.value = value;
    }

    public ValueIsPresentException(Result<?> value, Throwable cause) {
        super(cause);
        this.value = value;
    }

    public ValueIsPresentException(Result<?> value, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.value = value;
    }

    public Result<?> getValue() {
        return value;
    }

    @Override
    public Scope getScope() {
        return value.getScope();
    }

    @Override
    public Status getStatus() {
        return DefaultStatus.INTERNAL_ERROR;
    }

    @Override
    public RuntimeException getException() throws ValueIsPresentException {
        return this;
    }
}
