package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.DefaultStatus;
import org.cthul.monad.util.GenericScope;
import org.cthul.monad.util.ScopedResult;

public class NoSuchElement<T, X extends Exception> extends ArgumentError<T, X> {
    
    public static final String MESSAGE = "No such element";

    public NoSuchElement(GenericScope<? extends X> scope, Object context, String operation, String parameter, Class<T> expected) {
        super(context, operation, parameter, expected, null, scope.noValue(DefaultStatus.NOT_FOUND, MESSAGE));
    }

    public NoSuchElement(Object context, String operation, String parameter, Class<T> expected, ScopedResult<?, ? extends X> result) {
        super(context, operation, parameter, expected, null, result);
    }

    public NoSuchElement(Object context, String operation, String parameter, Class<T> expected, X exception) {
        super(context, operation, parameter, expected, null, MESSAGE, exception);
    }

    public NoSuchElement(Object context, String operation, String parameter, Class<T> expected, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, null, MESSAGE, exceptionSource);
    }
}
