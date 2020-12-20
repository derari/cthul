package org.cthul.monad.error;

import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.cthul.monad.GenericScope;
import org.cthul.monad.ScopedResult;

public class IllegalArgument<T, X extends Exception> extends ArgumentError<T, X> {
    
    public static <T, X extends Exception> IAActualValueStep<T, IllegalArgument<T, X>> unprocessable(Class<T> expected, GenericScope<X> scope, String message) {
        return new IABuilder<>(expected, exception(scope), message);
    }
    
    public static <T, X extends Exception> IAActualValueStep<T, IllegalArgument<T, X>> unprocessable(Class<T> expected, X exception) {
        return new IABuilder<>(expected, exception(exception), exception.getMessage());
    }
    
    public static <T, X extends Exception> IAActualValueStep<T, IllegalArgument<T, X>> unprocessable(Class<T> expected, ScopedResult<?, X> result) {
        return new IABuilder<>(expected, exception(result), result.getMessage());
    }

    public IllegalArgument(Operation operation, Parameter<T> parameter, Object value, ScopedResult<?, ? extends X> result) {
        super(operation, parameter, value, result);
    }

    public IllegalArgument(Operation operation, Parameter<T> parameter, Object value, X exception) {
        super(operation, parameter, value, exception);
    }

    public IllegalArgument(Operation operation, Parameter<T> parameter, Object value, Function<? super ErrorState<?>, ? extends X> exceptionSource, String message) {
        super(operation, parameter, value, exceptionSource, message);
    }

    protected IllegalArgument(Builder<T, X, ?> builder) {
        super(builder);
    }

    @Override
    public T getValue() {
        if (isResolved()) return peekResolvedValue();
        return (T) super.getValue();
    }

    public void setValue(T resolvedValue) {
        setResolvedValue(resolvedValue);
    }
    
    public static interface IAActualValueStep<T, State> {
        
        OperationStep<State> got(@Nullable T value);
    }
    
    private static class IABuilder<T, X extends Exception> extends Builder<T, X, IllegalArgument<T, X>> implements IAActualValueStep<T, IllegalArgument<T, X>> {

        public IABuilder(Class<T> expected, Function<? super ErrorState<?>, ? extends X> exceptionSource, String message) {
            super(expected, exceptionSource, message);
        }
        
        @Override
        protected IllegalArgument<T, X> build() {
            return new IllegalArgument<>(this);
        }
    }
}
