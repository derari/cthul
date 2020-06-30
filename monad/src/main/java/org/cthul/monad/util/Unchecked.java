package org.cthul.monad.util;

import org.cthul.monad.error.Checked;
import org.cthul.monad.error.ErrorHandler;
import org.cthul.monad.error.ErrorHandlerLayer;

public interface Unchecked {
    
    @FunctionalInterface
    interface Runnable<X extends RuntimeException> extends Checked.Runnable<X>, java.lang.Runnable {

        @Override
        default Unchecked.Runnable<X> with(ErrorHandler errorHandler) {
            return Checked.Runnable.super.with(errorHandler)::run;
        }

        @Override
        default Unchecked.Runnable<X> with(ErrorHandlerLayer errorHandlerLayer) {
            return Checked.Runnable.super.with(errorHandlerLayer)::run;
        }
    }
    
    @FunctionalInterface
    interface Consumer<T, X extends RuntimeException> extends Checked.Consumer<T, X>, java.util.function.Consumer<T> {
        
        @Override
        default Unchecked.Consumer<T, X> with(ErrorHandler errorHandler) {
            return Checked.Consumer.super.with(errorHandler)::accept;
        }

        @Override
        default Unchecked.Consumer<T, X> with(ErrorHandlerLayer errorHandlerLayer) {
            return Checked.Consumer.super.with(errorHandlerLayer)::accept;
        }
    }
    
    @FunctionalInterface
    interface Supplier<T, X extends RuntimeException> extends Checked.Supplier<T, X>, java.util.function.Supplier<T> {
        
        @Override
        default Unchecked.Supplier<T, X> with(ErrorHandler errorHandler) {
            return Checked.Supplier.super.with(errorHandler)::get;
        }

        @Override
        default Unchecked.Supplier<T, X> with(ErrorHandlerLayer errorHandlerLayer) {
            return Checked.Supplier.super.with(errorHandlerLayer)::get;
        }
    }
    
    @FunctionalInterface
    interface Function<T, R, X extends RuntimeException> extends Checked.Function<T, R, X>, java.util.function.Function<T, R> {
        
        @Override
        default Unchecked.Function<T, R, X> with(ErrorHandler errorHandler) {
            return Checked.Function.super.with(errorHandler)::apply;
        }

        @Override
        default Unchecked.Function<T, R, X> with(ErrorHandlerLayer errorHandlerLayer) {
            return Checked.Function.super.with(errorHandlerLayer)::apply;
        }
    }
    
    @FunctionalInterface
    interface BiConsumer<T, U, X extends RuntimeException> extends Checked.BiConsumer<T, U, X>, java.util.function.BiConsumer<T, U> {
        
        @Override
        default Unchecked.BiConsumer<T, U, X> with(ErrorHandler errorHandler) {
            return Checked.BiConsumer.super.with(errorHandler)::accept;
        }

        @Override
        default Unchecked.BiConsumer<T, U, X> with(ErrorHandlerLayer errorHandlerLayer) {
            return Checked.BiConsumer.super.with(errorHandlerLayer)::accept;
        }
    }
    
    @FunctionalInterface
    interface BiFunction<T, U, R, X extends RuntimeException> extends Checked.BiFunction<T, U, R, X>, java.util.function.BiFunction<T, U, R> {
        
        @Override
        default Unchecked.BiFunction<T, U, R, X> with(ErrorHandler errorHandler) {
            return Checked.BiFunction.super.with(errorHandler)::apply;
        }

        @Override
        default Unchecked.BiFunction<T, U, R, X> with(ErrorHandlerLayer errorHandlerLayer) {
            return Checked.BiFunction.super.with(errorHandlerLayer)::apply;
        }
    }
}
