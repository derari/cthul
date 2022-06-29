package org.cthul.monad.util;

import java.util.NoSuchElementException;
import org.cthul.monad.DefaultStatus;
import org.cthul.monad.Status;

public class RuntimeExceptionType implements ExceptionType<RuntimeException> {

    private static final RuntimeExceptionType INSTANCE = new RuntimeExceptionType();

    public static RuntimeExceptionType getInstance() {
        return INSTANCE;
    }

    @Override
    public RuntimeException exception(Status status, String message, Throwable cause) {
        if (message == null) {
            if (cause instanceof RuntimeException) {
                return (RuntimeException) cause;
            }
            message = cause != null && cause.getMessage() != null ? cause.getMessage()
                    : status.getDescription();
        }
        if (status.getCode() == DefaultStatus.NO_VALUE.getCode()) {
            return new NoSuchElementException(message, cause);
        }
        return new IllegalArgumentException(message, cause);
    }
}
