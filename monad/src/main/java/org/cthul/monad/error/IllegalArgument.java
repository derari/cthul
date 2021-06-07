package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.Unsafe;
import org.cthul.monad.util.ExceptionType;
public class IllegalArgument<T, X extends Exception> extends ArgumentError<T, X> {

    public IllegalArgument(Object context, String operation, String parameter, Class<T> expected, Object value, Unsafe<?, ? extends X> result) {
        super(context, operation, parameter, expected, value, result);
    }

    public IllegalArgument(Object context, String operation, String parameter, Class<T> expected, Object value, String error, X exception) {
        super(context, operation, parameter, expected, value, error, exception);
    }

    public IllegalArgument(Object context, String operation, String parameter, Class<T> expected, Object value, String error, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, value, error, exceptionSource);
    }

    public IllegalArgument(Object context, String operation, String parameter, Class<T> expected, Object value, ExceptionType<? extends X> exceptionType, String error, Object... args) {
        super(context, operation, parameter, expected, value, exceptionType, error, args);
    }

    protected IllegalArgument(ArgumentError<T, ? extends X> source) {
        super(source);
    }

    @Override
    public T getValue() {
        if (isResolved()) return peekResolvedValue();
        return (T) super.getValue();
    }

    public void setValue(T resolvedValue) {
        setResolvedValue(resolvedValue);
    }
}
