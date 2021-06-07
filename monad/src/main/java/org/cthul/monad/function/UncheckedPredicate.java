package org.cthul.monad.function;

import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

@FunctionalInterface
public interface UncheckedPredicate<T, X extends RuntimeException> extends CheckedPredicate<T, X>, java.util.function.Predicate<T> {

    @Override
    default UncheckedPredicate<T, X> with(ErrorHandler errorHandler) {
        return CheckedPredicate.super.with(errorHandler)::test;
    }

    @Override
    default UncheckedPredicate<T, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return CheckedPredicate.super.with(errorHandlerLayer)::test;
    }
}
