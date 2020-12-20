package org.cthul.monad.error;

import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.Nullable;
import static org.cthul.monad.error.GeneralErrorState.exception;
import org.cthul.monad.GenericScope;
import org.cthul.monad.ScopedResult;

public class ConversionFailed<S, T, X extends Exception> extends ArgumentError<T, X> {
    
    public static <T, X extends Exception> CFActualValueStep<T, X> unprocessable(Class<T> expected, GenericScope<X> scope, String message) {
        return new CFBuilder<>(expected, exception(scope), message);
    }

    public static <T, X extends Exception> CFActualValueStep<T, X> unprocessable(Class<T> expected, X exception) {
        return new CFBuilder<>(expected, exception(exception), exception.getMessage());
    }

    public static <T, X extends Exception> CFActualValueStep<T, X> unprocessable(Class<T> expected, ScopedResult<?, X> result) {
        return new CFBuilder<>(expected, exception(result), result.getMessage());
    }

    public ConversionFailed(Operation operation, Parameter<T> parameter, Object value, ScopedResult<?, ? extends X> result) {
        super(operation, parameter, value, result);
    }

    public ConversionFailed(Operation operation, Parameter<T> parameter, Object value, X exception) {
        super(operation, parameter, value, exception);
    }

    public ConversionFailed(Operation operation, Parameter<T> parameter, Object value, Function<? super ErrorState<?>, ? extends X> exceptionSource, String message) {
        super(operation, parameter, value, exceptionSource, message);
    }

    protected ConversionFailed(Builder<T, X, ?> builder) {
        super(builder);
    }

    @Override
    public S getValue() {
        return (S) super.getValue();
    }
    
    public static interface CFActualValueStep<T, X extends Exception> {

        <S> OperationStep<ConversionFailed<S, T, X>> from(@Nullable S value);
    }

    private static class CFBuilder<S, T, X extends Exception> extends Builder<T, X, ConversionFailed<S, T, X>> implements CFActualValueStep<T, X> {

        public CFBuilder(Class<T> expected, Function<? super ErrorState<?>, ? extends X> exceptionSource, String message) {
            super(expected, exceptionSource, message);
        }

        @Override
        protected ConversionFailed<S, T, X> build() {
            return new ConversionFailed<>(this);
        }

        @Override
        public <S> OperationStep<ConversionFailed<S, T, X>> from(S value) {
            return (OperationStep) this.got(value);
        }
    }
}
