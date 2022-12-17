package org.cthul.monad;

import org.cthul.monad.result.ValueResult;

public class ValueIsPresentException extends ScopedRuntimeException {

    private final ValueResult<?> value;

    public ValueIsPresentException(ValueResult<?> value) {
        this(value, value.toString());
    }

    public ValueIsPresentException(ValueResult<?> value, String message) {
        super(value.getScope(), DefaultStatus.INTERNAL_ERROR, message);
        this.value = value;
    }

    public ValueIsPresentException(ValueResult<?> value, String message, Throwable cause) {
        super(value.getScope(), DefaultStatus.INTERNAL_ERROR, message, cause);
        this.value = value;
    }

    public ValueIsPresentException(ValueResult<?> value, Throwable cause) {
        this(value, cause.getMessage(), cause);
    }

    public ValueResult<?> getValue() {
        return value;
    }
}
