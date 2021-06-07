package org.cthul.monad.function;

import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;
import org.cthul.monad.error.ErrorHandlingScope;

public interface CheckedConsumer<T, X extends Exception> {

    static <T> UncheckedConsumer<T, RuntimeException> from(java.util.function.Consumer<T> consumer) {
        return consumer::accept;
    }
    
    void accept(T t) throws X;

    default CheckedConsumer<T, X> with(ErrorHandler errorHandler) {
        return t -> {
            try (final ErrorHandlingScope context = errorHandler.enable()) {
                accept(t);
            }
        };
    }

    default CheckedConsumer<T, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return t -> {
            try (final ErrorHandlingScope context = errorHandlerLayer.enable()) {
                accept(t);
            }
        };
    }
}
