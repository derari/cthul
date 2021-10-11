package org.cthul.monad;

public class ValueIsPresentException extends ScopedRuntimeException {

    private final Result<?> value;

    public ValueIsPresentException(Result<?> value) {
        this(value, value.toString());
    }

    public ValueIsPresentException(Result<?> value, String message) {
        super(value.getScope(), DefaultStatus.INTERNAL_ERROR, message);
        this.value = value;
    }

    public ValueIsPresentException(Result<?> value, String message, Throwable cause) {
        super(value.getScope(), DefaultStatus.INTERNAL_ERROR, message, cause);
        this.value = value;
    }

    public ValueIsPresentException(Result<?> value, Throwable cause) {
        this(value, cause.getMessage(), cause);
    }

    public Result<?> getValue() {
        return value;
    }
}
