package org.cthul.monad.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.Result;
import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.result.NoResult;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ValueResult;
import org.cthul.monad.switches.BasicSwitch;
import org.cthul.monad.util.UnsafeStatusAdapter;

public interface RuntimeExceptionWrapper<X extends RuntimeException> {
    
    default <T> Result<T> result(T value, Exception exception) {
        if (exception == null) 
            return value(value);
        return failed(exception).asUnsafe();
    }
     
    NoResult failed(Exception exception);
     
    <T> ValueResult<T> value(T value);
    
    NoResult okNoValue();
    
    default ExceptionWrapper<X> typed() {
        return new ExceptionWrapper<X>() {
            @Override
            public NoValue<X> failed(Exception exception) {
                return (NoValue) RuntimeExceptionWrapper.this.failed(exception);
            }
            @Override
            public <T> ValueResult<T> value(T value) {
                return RuntimeExceptionWrapper.this.value(value);
            }
            @Override
            public NoResult okNoValue() {
                return RuntimeExceptionWrapper.this.okNoValue();
            }
            @Override
            public RuntimeExceptionWrapper<X> unchecked() {
                return RuntimeExceptionWrapper.this;
            }
            @Override
            public String toString() {
                return RuntimeExceptionWrapper.this.toString();
            }
        };
    }
    
    default RuntimeExceptionWrapper withStatus(Status status) {
        return withStatus().orElse().set(status);
    }
    
    default BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, Status, ? extends RuntimeExceptionWrapper<X>> withStatus() {
        return new UnsafeStatusAdapter.Unchecked(this).withStatus();
    }
    
    default BasicSwitch<Scope, Scope, Status, ? extends RuntimeExceptionWrapper<X>> withStatusByScope() {
        return BasicSwitch.asBasicSwitch(withStatus().mapKey(Unsafe::getScope).mapSource(Unsafe::getScope));
    }
    
    default BasicSwitch<Exception, Exception, Status, ? extends RuntimeExceptionWrapper<X>> withStatusByException() {
        return (BasicSwitch) BasicSwitch.asBasicSwitch(withStatus().mapKey(Unsafe::getException).mapSource(Unsafe::getException));
    }
    
    default Safe<X> safe() {
        return () -> this;
    }
    
    default Wrap<X> wrap() {
        ExceptionWrapper<X> typed = typed();
        return () -> typed;
    }
    
    interface Safe<X extends RuntimeException> {
        
        RuntimeExceptionWrapper<X> exceptionWrapper();
        
        default ExceptionWrapper.Safe<X> typed() {
            return exceptionWrapper().typed().safe();
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T> Function<T, Result<?>> consumer(CheckedConsumer<T, ?> consumer) {
            return t -> {
                try {
                    consumer.accept(t);
                    return exceptionWrapper().okNoValue();
                } catch (Exception x) {
                    return exceptionWrapper().failed(x);
                }
            };
        }

        default <T> Result<?> accept(T value, CheckedConsumer<T, ?> consumer) {
            return consumer(consumer).apply(value);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U> BiFunction<T, U, Result<?>> biconsumer(CheckedBiConsumer<T, U, ?> consumer) {
            return (t, u) -> {
                try {
                    consumer.accept(t, u);
                    return exceptionWrapper().okNoValue();
                } catch (Exception x) {
                    return exceptionWrapper().failed(x);
                }
            };
        }

        default <T, U> Result<?> accept(T value1, U value2, CheckedBiConsumer<T, U, ?> consumer) {
            return biconsumer(consumer).apply(value1, value2);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, R> Function<T, Result<R>> function(CheckedFunction<T, R, ?> function) {
            return t -> {
                try {
                    R r = function.apply(t);
                    return exceptionWrapper().value(r);
                } catch (Exception x) {
                    return exceptionWrapper().failed(x).asUnsafe();
                }
            };
        }

        default <T, R> Result<R> apply(T value, CheckedFunction<T, R, ?> function) {
            return function(function).apply(value);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U, R> BiFunction<T, U, Result<R>> bifunction(CheckedBiFunction<T, U, R, ?> function) {
            return (t, u) -> {
                try {
                    R r = function.apply(t, u);
                    return exceptionWrapper().value(r);
                } catch (Exception x) {
                    return exceptionWrapper().failed(x).asUnsafe();
                }
            };
        }

        default <T, U, R> Result<R> apply(T value1, U value2, CheckedBiFunction<T, U, R, ?> function) {
            return bifunction(function).apply(value1, value2);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T> Function<T, Result<Boolean>> predicate(CheckedPredicate<T, ?> predicate) {
            return t -> {
                try {
                    Boolean r = predicate.test(t);
                    return exceptionWrapper().value(r);
                } catch (Exception x) {
                    return exceptionWrapper().failed(x).asUnsafe();
                }
            };
        }

        default <T> Result<Boolean> test(T value, CheckedPredicate<T, ?> predicate) {
            return predicate(predicate).apply(value);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U> BiFunction<T, U, Result<Boolean>> bipredicate(CheckedBiPredicate<T, U, ?> predicate) {
            return (t, u) -> {
                try {
                    Boolean r = predicate.test(t, u);
                    return exceptionWrapper().value(r);
                } catch (Exception x) {
                    return exceptionWrapper().failed(x).asUnsafe();
                }
            };
        }

        default <T, U> Result<Boolean> test(T value1, U value2, CheckedBiPredicate<T, U, ?> predicate) {
            return bipredicate(predicate).apply(value1, value2);
        }
        
        @SuppressWarnings("UseSpecificCatch")
        default Supplier<Result<?>> runnable(CheckedRunnable<?> runnable) {
            return () -> {
                try {
                    runnable.run();
                    return exceptionWrapper().okNoValue();
                } catch (Exception x) {
                    return exceptionWrapper().failed(x);
                }
            };
        }

        default Unsafe<?, ?> run(CheckedRunnable<?> runnable) {
            return runnable(runnable).get();
        }
        
        @SuppressWarnings("UseSpecificCatch")
        default <T> Supplier<Result<T>> supplier(CheckedSupplier<T, ?> supplier) {
            return () -> {
                try {
                    T t = supplier.get();
                    return exceptionWrapper().value(t);
                } catch (Exception x) {
                    return exceptionWrapper().failed(x).asUnsafe();
                }
            };
        }

        default <T> Result<T> get(CheckedSupplier<T, ?> supplier) {
            return supplier(supplier).get();
        }
    }
    
    interface Wrap<X extends RuntimeException> extends ExceptionWrapper.Wrap<X> {
        
        @Override
        default <T> UncheckedConsumer<T, X> consumer(CheckedConsumer<T, ?> consumer) {
            return t -> accept(t, consumer);
        }

        @Override
        default <T, U> UncheckedBiConsumer<T, U, X> biconsumer(CheckedBiConsumer<T, U, ?> consumer) {
            return (t, u) -> accept(t, u, consumer);
        }

        @Override
        default <T, R> UncheckedFunction<T, R, X> function(CheckedFunction<T, R, ?> function) {
            return t -> apply(t, function);
        }

        @Override
        default <T, U, R> UncheckedBiFunction<T, U, R, X> bifunction(CheckedBiFunction<T, U, R, ?> function) {
            return (t, u) -> apply(t, u, function);
        }

        @Override
        default <T> UncheckedPredicate<T, X> predicate(CheckedPredicate<T, ?> predicate) {
            return t -> test(t, predicate);
        }

        @Override
        default <T, U> UncheckedBiPredicate<T, U, X> bipredicate(CheckedBiPredicate<T, U, ?> predicate) {
            return (t, u) -> test(t, u, predicate);
        }
        
        @Override
        default UncheckedRunnable<X> runnable(CheckedRunnable<?> runnable) {
            return () -> run(runnable);
        }
        
        @Override
        default <T> UncheckedSupplier<T, X> supplier(CheckedSupplier<T, ?> supplier) {
            return () -> get(supplier);
        }
    }
}
