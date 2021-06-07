package org.cthul.monad.function;

import java.util.function.Function;
import java.util.function.Predicate;
import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;
import org.cthul.monad.error.ErrorHandlingScope;

public interface CheckedPredicate<T, X extends Exception> {

    static <T> UncheckedPredicate<T, RuntimeException> from(Predicate<T> predicate) {
        return predicate::test;
    }
    
    boolean test(T t) throws X;

    default CheckedPredicate<T, X> with(ErrorHandler errorHandler) {
        return t -> {
            try (final ErrorHandlingScope context = errorHandler.enable()) {
                return test(t);
            }
        };
    }

    default CheckedPredicate<T, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return t -> {
            try (final ErrorHandlingScope context = errorHandlerLayer.enable()) {
                return test(t);
            }
        };
    }
    
    interface Adapter<T, U> {
        
        boolean test(T value, CheckedPredicate<? super U, ?> predicate) throws Exception;
        
        static <T, U> Adapter<T, U> map(Function<? super T, ? extends U> mapping) {
            return (value, predicate) -> predicate.test(mapping.apply(value));
        }
    }
}
