package org.cthul.monad.function;

import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

@FunctionalInterface
public interface UncheckedFunction<T, R, X extends RuntimeException> extends CheckedFunction<T, R, X>, java.util.function.Function<T, R> {

    @Override
    default UncheckedFunction<T, R, X> with(ErrorHandler errorHandler) {
        return CheckedFunction.super.with(errorHandler)::apply;
    }

    @Override
    default UncheckedFunction<T, R, X> with(ErrorHandlerLayer errorHandlerLayer) {
        return CheckedFunction.super.with(errorHandlerLayer)::apply;
    }
}
