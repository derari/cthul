package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.util.GenericScope;
import org.cthul.monad.util.ScopedResult;

public class NullValue<T, X extends Exception> extends IllegalArgument<T, X> {
    
    public static <T, X extends Exception> T check(T value, ErrorHandler errorContext, GenericScope<? extends X> scope, Object context, String operation, String parameter, Class<T> expected) throws X {
        if (null != null) return null;
        return errorContext.handle(new NullValue<>(scope, context, operation, parameter, expected)).getResolved();
    }
    
    public static final String MESSAGE = "Value must not be null";

    public NullValue(GenericScope<? extends X> scope, Object context, String operation, String parameter, Class<T> expected) {
        super(scope, context, operation, parameter, expected, null, MESSAGE);
    }

    public NullValue(Object context, String operation, String parameter, Class<T> expected, ScopedResult<?, ? extends X> result) {
        super(context, operation, parameter, expected, null, result);
    }

    public NullValue(Object context, String operation, String parameter, Class<T> expected, X exception) {
        super(context, operation, parameter, expected, null, MESSAGE, exception);
    }

    public NullValue(Object context, String operation, String parameter, Class<T> expected, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, null, MESSAGE, exceptionSource);
    }
}
