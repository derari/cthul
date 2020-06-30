package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.util.GenericScope;
import org.cthul.monad.util.ScopedResult;

public class IllegalArgument<T, X extends Exception> extends ArgumentError<T, X> {

    public IllegalArgument(GenericScope<? extends X> scope, Object context, String operation, String parameter, Class<T> expected, T value, String error) {
        super(scope, context, operation, parameter, expected, value, error);
    }

    public IllegalArgument(Object context, String operation, String parameter, Class<T> expected, T value, ScopedResult<?, ? extends X> result) {
        super(context, operation, parameter, expected, value, result);
    }

    public IllegalArgument(Object context, String operation, String parameter, Class<T> expected, T value, String error, X exception) {
        super(context, operation, parameter, expected, value, error, exception);
    }

    public IllegalArgument(Object context, String operation, String parameter, Class<T> expected, T value, String error, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, value, error, exceptionSource);
    }

    @Override
    public T getValue() {
        if (isResolved()) return getResolvedValue();
        return (T) super.getValue();
    }

    public void setValue(T resolvedValue) {
        setResolvedValue(resolvedValue);
    }
}
