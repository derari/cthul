package org.cthul.monad.function;

import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

@FunctionalInterface
public interface CheckedRunnable<X extends Exception> {

    static UncheckedRunnable<RuntimeException> from(java.lang.Runnable runnable) {
        return runnable::run;
    }

    void run() throws X;

    default <Y extends Exception> CheckedRunnable<Y> checked(Function<? super Exception, ? extends Y> errorHandler) {
        return () -> {
            try {
                run();
            } catch (Exception x) {
                throw errorHandler.apply(x);
            }
        };
    }
    
    default CheckedRunnable<X> with(ErrorHandler errorHandler) {
        return withSafeClosable(errorHandler::enable);
    }

    default CheckedRunnable<X> with(ErrorHandlerLayer errorHandlerLayer) {
        return withSafeClosable(errorHandlerLayer::enable);
    }
    
    default CheckedRunnable<X> withSafeClosable(Supplier<? extends UncheckedAutoClosable> closableSource) {
        return with((Supplier) closableSource);
    }

    default CheckedRunnable<X> with(Supplier<? extends CheckedAutoClosable<? extends X>> closableSource) {
        return () -> {
            try (CheckedAutoClosable<X> closable = (CheckedAutoClosable) closableSource.get()) {
                run();
            }
        };
    }
}
