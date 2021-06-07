package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.Unsafe;
import org.cthul.monad.util.ExceptionType;

public class ConversionFailed<S, T, X extends Exception> extends ArgumentError<T, X> {

    public ConversionFailed(Object context, String operation, String parameter, Class<T> expected, Object value, Unsafe<?, ? extends X> result) {
        super(context, operation, parameter, expected, value, result);
    }

    public ConversionFailed(Object context, String operation, String parameter, Class<T> expected, Object value, String error, X exception) {
        super(context, operation, parameter, expected, value, error, exception);
    }

    public ConversionFailed(Object context, String operation, String parameter, Class<T> expected, Object value, String error, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, value, error, exceptionSource);
    }

    public ConversionFailed(Object context, String operation, String parameter, Class<T> expected, Object value, ExceptionType<? extends X> exceptionType, String error, Object... args) {
        super(context, operation, parameter, expected, value, exceptionType, error, args);
    }

    public ConversionFailed(Object context, String operation, String parameter, Class<T> expected, Object value, ExceptionType<? extends X> exceptionType) {
        super(context, operation, parameter, expected, value, exceptionType, "Expected %s, got %s", expected, value);
    }

    protected ConversionFailed(ArgumentError<T, ? extends X> source) {
        super(source);
    }

    @Override
    public S getValue() {
        return (S) super.getValue();
    }
}
