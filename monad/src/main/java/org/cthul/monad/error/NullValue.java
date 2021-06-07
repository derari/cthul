package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.Unsafe;
import org.cthul.monad.util.ExceptionType;

public class NullValue<T, X extends Exception> extends IllegalArgument<T, X> {
    
    public static <T, X extends Exception> T check(T value, ErrorHandler errorContext, Object context, String operation, String parameter, Class<T> expected, ExceptionType<? extends X> exceptionType) throws X {
        if (value != null) return value;
        return new NullValue<>(context, operation, parameter, expected, exceptionType).handledOnce(errorContext).getResolved();
    }

    public NullValue(Object context, String operation, String parameter, Class<T> expected, Unsafe<?, ? extends X> result) {
        super(context, operation, parameter, expected, null, result);
    }

    public NullValue(Object context, String operation, String parameter, Class<T> expected, String error, X exception) {
        super(context, operation, parameter, expected, null, error, exception);
    }

    public NullValue(Object context, String operation, String parameter, Class<T> expected, String error, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, null, error, exceptionSource);
    }

    public NullValue(Object context, String operation, String parameter, Class<T> expected, ExceptionType<? extends X> exceptionType, String error, Object... args) {
        super(context, operation, parameter, expected, null, exceptionType, error, args);
    }

    public NullValue(Object context, String operation, String parameter, Class<T> expected, ExceptionType<? extends X> exceptionType) {
        super(context, operation, parameter, expected, null, exceptionType, "Value must not be null");
    }

    protected NullValue(ArgumentError<T, ? extends X> source) {
        super(source);
    }
}
