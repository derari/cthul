package org.cthul.monad.function;

import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;
import org.cthul.monad.error.ErrorHandlingScope;

public interface CheckedFunction<T, R, X extends Exception> {

    static <T, R> UncheckedFunction<T, R, RuntimeException> from(java.util.function.Function<T, R> function) {
        return function::apply;
    }
    
    R apply(T t) throws X;

    default CheckedFunction<T, R, X> with(ErrorHandler errorHandler) {
        return t -> {
            try (final ErrorHandlingScope context = errorHandler.enable()) {
                return apply(t);
            }
        };
    }

    default CheckedFunction<T, R, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return t -> {
            try (final ErrorHandlingScope context = errorHandlerLayer.enable()) {
                return apply(t);
            }
        };
    }
}
