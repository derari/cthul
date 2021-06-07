package org.cthul.monad.function;

import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

@FunctionalInterface
public interface UncheckedBiConsumer<T, U, X extends RuntimeException> extends CheckedBiConsumer<T, U, X>, java.util.function.BiConsumer<T, U> {

    @Override
    default UncheckedBiConsumer<T, U, X> with(ErrorHandler errorHandler) {
        return CheckedBiConsumer.super.with(errorHandler)::accept;
    }

    @Override
    default UncheckedBiConsumer<T, U, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return CheckedBiConsumer.super.with(errorHandlerLayer)::accept;
    }
}
