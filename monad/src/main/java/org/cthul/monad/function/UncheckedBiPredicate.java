package org.cthul.monad.function;

import java.util.function.BiPredicate;
import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

@FunctionalInterface
public interface UncheckedBiPredicate<T, U, X extends RuntimeException> extends CheckedBiPredicate<T, U,  X>, BiPredicate<T, U> {

    @Override
    default UncheckedBiPredicate<T, U, X> with(ErrorHandler errorHandler) {
        return CheckedBiPredicate.super.with(errorHandler)::test;
    }

    @Override
    default UncheckedBiPredicate<T, U, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return CheckedBiPredicate.super.with(errorHandlerLayer)::test;
    }
}
