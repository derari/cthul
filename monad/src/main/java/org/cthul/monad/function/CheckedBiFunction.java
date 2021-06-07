package org.cthul.monad.function;

import java.util.function.BiFunction;
import java.util.function.Supplier;
import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

public interface CheckedBiFunction<T, U, R, X extends Exception> {
    
    static <T, U, R> UncheckedBiFunction<T, U, R, RuntimeException> from(BiFunction<T, U, R> biFunction) {
        return biFunction::apply;
    }

    R apply(T t, U u) throws X;

    default CheckedBiFunction<T, U, R, X> with(ErrorHandler errorHandler) {
        return withSafeClosable(errorHandler::enable);
    }

    default CheckedBiFunction<T, U, R, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return withSafeClosable(errorHandlerLayer::enable);
    }

    default CheckedBiFunction<T, U, R, X> withSafeClosable(Supplier<? extends UncheckedAutoClosable> closableSource) {
        return with((Supplier) closableSource);
    }
    
    default CheckedBiFunction<T, U, R, X> with(Supplier<? extends CheckedAutoClosable<? extends X>> closableSource) {
        return (t, u) -> {
            try (CheckedAutoClosable<X> closable = (CheckedAutoClosable) closableSource.get()) {
                return apply(t, u);
            }
        };
    }
}
