package org.cthul.monad.function;

import java.util.function.BiPredicate;
import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;
import org.cthul.monad.error.ErrorHandlingScope;

public interface CheckedBiPredicate<T, U, X extends Exception> {

    static <T, U> UncheckedBiPredicate<T, U, RuntimeException> from(BiPredicate<T, U> predicate) {
        return predicate::test;
    }
    
    boolean test(T t, U u) throws X;

    default CheckedBiPredicate<T, U, X> with(ErrorHandler errorHandler) {
        return (t, u) -> {
            try (final ErrorHandlingScope context = errorHandler.enable()) {
                return test(t, u);
            }
        };
    }

    default CheckedBiPredicate<T, U, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return (t, u) -> {
            try (final ErrorHandlingScope context = errorHandlerLayer.enable()) {
                return test(t, u);
            }
        };
    }
}
