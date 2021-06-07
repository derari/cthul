package org.cthul.monad.function;

import java.util.function.Supplier;
import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

public interface CheckedSupplier<T, X extends Exception> {

    static <T> UncheckedSupplier<T, RuntimeException> from(Supplier<T> supplier) {
        return supplier::get;
    }

    T get() throws X;

    default CheckedSupplier<T, X> with(ErrorHandler errorHandler) {
        return withSafeClosable(errorHandler::enable);
    }

    default CheckedSupplier<T, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return withSafeClosable(errorHandlerLayer::enable);
    }

    default CheckedSupplier<T, X> withSafeClosable(Supplier<? extends UncheckedAutoClosable> closableSource) {
        return with((Supplier) closableSource);
    }

    default CheckedSupplier<T, X> with(Supplier<? extends CheckedAutoClosable<? extends X>> closableSource) {
        return () -> {
            try (CheckedAutoClosable<X> closable = (CheckedAutoClosable) closableSource.get()) {
                return get();
            }
        };
    }
}
