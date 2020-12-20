package org.cthul.monad.error;

import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.DefaultStatus;
import static org.cthul.monad.error.GeneralErrorState.exception;
import org.cthul.monad.GenericScope;
import org.cthul.monad.ScopedResult;

public class NoSuchElement<T, X extends Exception> extends ArgumentError<T, X> {

    public static <T, X extends Exception> ActualValueStep<NoSuchElement<T, X>> unprocessable(Class<T> expected, GenericScope<X> scope, String message) {
        return new NSEBuilder<>(expected, exception(scope), message);
    }

    public static <T, X extends Exception> ActualValueStep<NoSuchElement<T, X>> unprocessable(Class<T> expected, X exception) {
        return new NSEBuilder<>(expected, exception(exception), exception.getMessage());
    }

    public static <T, X extends Exception> ActualValueStep<NoSuchElement<T, X>> unprocessable(Class<T> expected, ScopedResult<?, X> result) {
        return new NSEBuilder<>(expected, exception(result), result.getMessage());
    }
    
    public NoSuchElement(Operation operation, Parameter<T> parameter, Object value, ScopedResult<?, ? extends X> result) {
        super(operation, parameter, value, result);
    }

    public NoSuchElement(Operation operation, Parameter<T> parameter, Object value, X exception) {
        super(operation, parameter, value, exception);
    }

    public NoSuchElement(Operation operation, Parameter<T> parameter, Object value, Function<? super ErrorState<?>, ? extends X> exceptionSource, String message) {
        super(operation, parameter, value, exceptionSource, message);
    }

    public NoSuchElement(Builder<T, X, ?> builder) {
        super(builder);
    }
    
    private static class NSEBuilder<T, X extends Exception> extends Builder<T, X, NoSuchElement<T, X>>  {

        public NSEBuilder(Class<T> expected, Function<? super ErrorState<?>, ? extends X> exceptionSource, String message) {
            super(expected, exceptionSource, message);
        }

        @Override
        protected NoSuchElement<T, X> build() {
            return new NoSuchElement<>(this);
        }
    }
}
