package org.cthul.monad.error;

public interface Checked {
    
    static Unchecked.Runnable<RuntimeException> from(java.lang.Runnable runnable) {
        return runnable::run;
    }
    
    static <T> Unchecked.Consumer<T, RuntimeException> from(java.util.function.Consumer<T> consumer) {
        return consumer::accept;
    }
    
    static <T> Unchecked.Supplier<T, RuntimeException> from(java.util.function.Supplier<T> supplier) {
        return supplier::get;
    }
    
    static <T, R> Unchecked.Function<T, R, RuntimeException> from(java.util.function.Function<T, R> function) {
        return function::apply;
    }
    
    static <T, U> Unchecked.BiConsumer<T, U, RuntimeException> from(java.util.function.BiConsumer<T, U> biConsumer) {
        return biConsumer::accept;
    }
    
    static <T, U, R> Unchecked.BiFunction<T, U, R, RuntimeException> from(java.util.function.BiFunction<T, U, R> biFunction) {
        return biFunction::apply;
    }
    
    interface Runnable<X extends Exception> {
        
        void run() throws X;
        
        default <Y extends Exception> Runnable<Y> checked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return () -> {
                try {
                    run();
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y extends RuntimeException> Unchecked.Runnable<Y> unchecked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return () -> {
                try {
                    run();
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y> java.util.function.Supplier<Y> safe(java.util.function.BiFunction<?, ? super Exception, ? extends Y> resultHandler) {
            return () -> {
                try {
                    run();
                    return resultHandler.apply(null, null);
                } catch (Exception x) {
                    return resultHandler.apply(null, x);
                }
            };
        }
        
        default Runnable<X> with(ErrorHandler errorHandler) {
            return () -> {
                try (ErrorContext context = errorHandler.enable()) {
                    run();
                }
            };
        }
        
        default Runnable<X> with(ErrorHandlerLayer errorHandlerLayer) {
            return () -> {
                try (ErrorContext context = errorHandlerLayer.enable()) {
                    run();
                }
            };
        }
    }
    
    interface Supplier<T, X extends Exception> {
        
        T get() throws X;
        
        default <Y extends Exception> Supplier<T, Y> checked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return () -> {
                try {
                   return get();
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y extends RuntimeException> Unchecked.Supplier<T, Y> unchecked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return () -> {
                try {
                    return get();
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y> java.util.function.Supplier<Y> safe(java.util.function.BiFunction<? super T, ? super Exception, ? extends Y> resultHandler) {
            return () -> {
                try {
                    T t = get();
                    return resultHandler.apply(t, null);
                } catch (Exception x) {
                    return resultHandler.apply(null, x);
                }
            };
        }
        
        default Supplier<T, X> with(ErrorHandler errorHandler) {
            return () -> {
                try (ErrorContext context = errorHandler.enable()) {
                    return get();
                }
            };
        }
        
        default Supplier<T, X> with(ErrorHandlerLayer errorHandlerLayer) {
            return () -> {
                try (ErrorContext context = errorHandlerLayer.enable()) {
                    return get();
                }
            };
        }
    }
    
    interface Consumer<T, X extends Exception> {
        
        void accept(T t) throws X;
        
        default <Y extends Exception> Consumer<T, Y> checked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return t -> {
                try {
                   accept(t);
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y extends RuntimeException> Unchecked.Consumer<T, Y> unchecked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return t -> {
                try {
                    accept(t);
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y> java.util.function.Function<T, Y> safe(java.util.function.BiFunction<?, ? super Exception, ? extends Y> resultHandler) {
            return t -> {
                try {
                    accept(t);
                    return resultHandler.apply(null, null);
                } catch (Exception x) {
                    return resultHandler.apply(null, x);
                }
            };
        }
        
        default Consumer<T, X> with(ErrorHandler errorHandler) {
            return t -> {
                try (ErrorContext context = errorHandler.enable()) {
                    accept(t);
                }
            };
        }
        
        default Consumer<T, X> with(ErrorHandlerLayer errorHandlerLayer) {
            return t -> {
                try (ErrorContext context = errorHandlerLayer.enable()) {
                    accept(t);
                }
            };
        }
    }
    
    interface Function<T, R, X extends Exception> {
        
        R apply(T t) throws X;
        
        default <Y extends Exception> Function<T, R, Y> checked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return t -> {
                try {
                    return apply(t);
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y extends RuntimeException> Unchecked.Function<T, R, Y> unchecked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return t -> {
                try {
                    return apply(t);
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y> java.util.function.Function<T, Y> safe(java.util.function.BiFunction<? super R, ? super Exception, ? extends Y> resultHandler) {
            return t -> {
                try {
                    R r = apply(t);
                    return resultHandler.apply(r, null);
                } catch (Exception x) {
                    return resultHandler.apply(null, x);
                }
            };
        }
        
        default Function<T, R, X> with(ErrorHandler errorHandler) {
            return t -> {
                try (ErrorContext context = errorHandler.enable()) {
                    return apply(t);
                }
            };
        }
        
        default Function<T, R, X> with(ErrorHandlerLayer errorHandlerLayer) {
            return t -> {
                try (ErrorContext context = errorHandlerLayer.enable()) {
                    return apply(t);
                }
            };
        }
    }
    
    interface BiConsumer<T, U, X extends Exception> {
        
        void accept(T t, U u) throws X;
        
        default <Y extends Exception> BiConsumer<T, U, Y> checked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return (t, u) -> {
                try {
                    accept(t, u);
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y extends RuntimeException> Unchecked.BiConsumer<T, U, Y> unchecked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return (t, u) -> {
                try {
                    accept(t, u);
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y> java.util.function.BiFunction<T, U, Y> safe(java.util.function.BiFunction<?, ? super Exception, ? extends Y> resultHandler) {
            return (t, u) -> {
                try {
                    accept(t, u);
                    return resultHandler.apply(null, null);
                } catch (Exception x) {
                    return resultHandler.apply(null, x);
                }
            };
        }
        
        default BiConsumer<T, U, X> with(ErrorHandler errorHandler) {
            return (t, u) -> {
                try (ErrorContext context = errorHandler.enable()) {
                    accept(t, u);
                }
            };
        }
        
        default BiConsumer<T, U, X> with(ErrorHandlerLayer errorHandlerLayer) {
            return (t, u) -> {
                try (ErrorContext context = errorHandlerLayer.enable()) {
                    accept(t, u);
                }
            };
        }
    }
    
    interface BiFunction<T, U, R, X extends Exception> {
        
        R apply(T t, U u) throws X;
        
        default <Y extends Exception> BiFunction<T, U, R, Y> checked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return (t, u) -> {
                try {
                    return apply(t, u);
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y extends RuntimeException> Unchecked.BiFunction<T, U, R, Y> unchecked(java.util.function.Function<? super Exception, ? extends Y> errorHandler) {
            return (t, u) -> {
                try {
                    return apply(t, u);
                } catch (Exception x) {
                    throw errorHandler.apply(x);
                }
            };
        }
        
        default <Y> java.util.function.BiFunction<T, U, Y> safe(java.util.function.BiFunction<? super R, ? super Exception, ? extends Y> resultHandler) {
            return (t, u) -> {
                try {
                    R r = apply(t, u);
                    return resultHandler.apply(r, null);
                } catch (Exception x) {
                    return resultHandler.apply(null, x);
                }
            };
        }
        
        default BiFunction<T, U, R, X> with(ErrorHandler errorHandler) {
            return (t, u) -> {
                try (ErrorContext context = errorHandler.enable()) {
                    return apply(t, u);
                }
            };
        }
        
        default BiFunction<T, U, R, X> with(ErrorHandlerLayer errorHandlerLayer) {
            return (t, u) -> {
                try (ErrorContext context = errorHandlerLayer.enable()) {
                    return apply(t, u);
                }
            };
        }
    }
}
