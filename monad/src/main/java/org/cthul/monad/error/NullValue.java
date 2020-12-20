package org.cthul.monad.error;

import java.util.function.Function;
import org.cthul.monad.GenericScope;
import org.cthul.monad.ScopedResult;

public class NullValue<T, X extends Exception> extends IllegalArgument<T, X> {
    
    public static <T, X extends Exception> OperationStep<NullValue<T, X>> expected(Class<T> expected, GenericScope<X> scope) {
        return new NVBuilder<>(expected, exception(scope), "Value must not be null").got(null);
    }
    
    public static <T, X extends Exception> OperationStep<NullValue<T, X>> expected(Class<T> expected, GenericScope<X> scope, String message) {
        return new NVBuilder<>(expected, exception(scope), message).got(null);
    }

    public static <T, X extends Exception> OperationStep<NullValue<T, X>> expected(Class<T> expected, X exception) {
        return new NVBuilder<>(expected, exception(exception), exception.getMessage()).got(null);
    }

    public static <T, X extends Exception> OperationStep<NullValue<T, X>> expected(Class<T> expected, ScopedResult<?, X> result) {
        return new NVBuilder<>(expected, exception(result), result.getMessage()).got(null);
    }

    public NullValue(Operation operation, Parameter<T> parameter, Object value, ScopedResult<?, ? extends X> result) {
        super(operation, parameter, value, result);
    }

    public NullValue(Operation operation, Parameter<T> parameter, Object value, X exception) {
        super(operation, parameter, value, exception);
    }

    public NullValue(Operation operation, Parameter<T> parameter, Object value, Function<? super ErrorState<?>, ? extends X> exceptionSource, String message) {
        super(operation, parameter, value, exceptionSource, message);
    }

    public NullValue(Builder<T, X, ?> builder) {
        super(builder);
    }

    private static class NVBuilder<T, X extends Exception> extends Builder<T, X, NullValue<T, X>> {

        public NVBuilder(Class<T> expected, Function<? super ErrorState<?>, ? extends X> exceptionSource, String message) {
            super(expected, exceptionSource, message);
        }

        @Override
        protected NullValue<T, X> build() {
            return new NullValue<>(this);
        }
    }
}
