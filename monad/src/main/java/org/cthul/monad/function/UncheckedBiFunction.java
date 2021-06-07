package org.cthul.monad.function;

import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

@FunctionalInterface
public interface UncheckedBiFunction<T, U, R, X extends RuntimeException> extends CheckedBiFunction<T, U, R, X>, java.util.function.BiFunction<T, U, R> {

    @Override
    default UncheckedBiFunction<T, U, R, X> with(ErrorHandler errorHandler) {
        return CheckedBiFunction.super.with(errorHandler)::apply;
    }

    @Override
    default UncheckedBiFunction<T, U, R, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return CheckedBiFunction.super.with(errorHandlerLayer)::apply;
    }
}
