package org.cthul.monad.function;

import java.util.function.Supplier;
import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

@FunctionalInterface
public interface UncheckedSupplier<T, X extends RuntimeException> extends CheckedSupplier<T, X>, java.util.function.Supplier<T> {

    @Override
    default UncheckedSupplier<T, X> with(ErrorHandler errorHandler) {
        return CheckedSupplier.super.with(errorHandler)::get;
    }

    @Override
    default UncheckedSupplier<T, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return CheckedSupplier.super.with(errorHandlerLayer)::get;
    }

    @Override
    default UncheckedSupplier<T, X> withSafeClosable(Supplier<? extends UncheckedAutoClosable> closableSource) {
        return CheckedSupplier.super.withSafeClosable(closableSource)::get;
    }

    @Override
    default UncheckedSupplier<T, X> with(Supplier<? extends CheckedAutoClosable<? extends X>> closableSource) {
        return CheckedSupplier.super.with(closableSource)::get;
    }
}
