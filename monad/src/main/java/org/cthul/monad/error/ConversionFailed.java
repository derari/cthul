package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.util.GenericScope;
import org.cthul.monad.util.ScopedResult;

public class ConversionFailed<S, T, X extends Exception> extends ArgumentError<T, X> {
    
    public static String message(Class<?> expected, Object value) {
        return "Expected " + expected + ", got " + (value == null ? "null" : value.getClass());
    }

    public ConversionFailed(GenericScope<? extends X> scope, Object context, String operation, String parameter, Class<T> expected, S value, String error) {
        super(scope, context, operation, parameter, expected, value, error);
    }

    public ConversionFailed(Object context, String operation, String parameter, Class<T> expected, S value, ScopedResult<?, ? extends X> result) {
        super(context, operation, parameter, expected, value, result);
    }

    public ConversionFailed(Object context, String operation, String parameter, Class<T> expected, S value, String error, X exception) {
        super(context, operation, parameter, expected, value, error, exception);
    }

    public ConversionFailed(Object context, String operation, String parameter, Class<T> expected, S value, String error, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, value, error, exceptionSource);
    }

    public ConversionFailed(GenericScope<? extends X> scope, Object context, String operation, String parameter, Class<T> expected, S value) {
        super(scope, context, operation, parameter, expected, value, message(expected, value));
    }

    public ConversionFailed(Object context, String operation, String parameter, Class<T> expected, S value, X exception) {
        super(context, operation, parameter, expected, value, message(expected, value), exception);
    }

    public ConversionFailed(Object context, String operation, String parameter, Class<T> expected, S value, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, value, message(expected, value), exceptionSource);
    }

    @Override
    public S getValue() {
        return (S) super.getValue();
    }
}
