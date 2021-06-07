package org.cthul.monad.function;

import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

@FunctionalInterface
public interface UncheckedConsumer<T, X extends RuntimeException> extends CheckedConsumer<T, X>, java.util.function.Consumer<T> {

    @Override
    default UncheckedConsumer<T, X> with(ErrorHandler errorHandler) {
        return CheckedConsumer.super.with(errorHandler)::accept;
    }

    @Override
    default UncheckedConsumer<T, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return CheckedConsumer.super.with(errorHandlerLayer)::accept;
    }
}
