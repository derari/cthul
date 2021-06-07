package org.cthul.monad.function;

import java.util.function.BiConsumer;
import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;
import org.cthul.monad.error.ErrorHandlingScope;

public interface CheckedBiConsumer<T, U, X extends Exception> {

    static <T, U> UncheckedBiConsumer<T, U, RuntimeException> from(BiConsumer<T, U> biConsumer) {
        return biConsumer::accept;
    }
    
    void accept(T t, U u) throws X;

    default CheckedBiConsumer<T, U, X> with(ErrorHandler errorHandler) {
        return (t, u) -> {
            try (final ErrorHandlingScope context = errorHandler.enable()) {
                accept(t, u);
            }
        };
    }

    default CheckedBiConsumer<T, U, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return (t, u) -> {
            try (final ErrorHandlingScope context = errorHandlerLayer.enable()) {
                accept(t, u);
            }
        };
    }
}
