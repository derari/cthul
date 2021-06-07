package org.cthul.monad.function;

import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

@FunctionalInterface
public interface UncheckedRunnable<X extends RuntimeException> extends CheckedRunnable<X>, java.lang.Runnable {

    @Override
    default UncheckedRunnable<X> with(ErrorHandler errorHandler) {
        return CheckedRunnable.super.with(errorHandler)::run;
    }

    @Override
    default UncheckedRunnable<X> with(ErrorHandlerLayer errorHandlerLayer) {
        return CheckedRunnable.super.with(errorHandlerLayer)::run;
    }
}
