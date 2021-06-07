package org.cthul.monad.function;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.result.NoResult;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ValueResult;
import org.cthul.monad.switches.BasicSwitch;

public interface ResultWrapper {
    
    default <T, X extends Exception> Unsafe<T, X> result(T value, X exception) {
        if (exception == null) 
            return value(value).value();
        return failed(exception).asUnsafe();
    }
     
    <X extends Exception> NoValue<X> failed(X exception);
     
    <T> ValueResult<T> value(T value);
    
    NoResult okNoValue();
    
    default ResultWrapper withStatus(Status status) {
        return withStatus().orElse().set(status);
    }
    
    BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, Status, ? extends ResultWrapper> withStatus();
    
    default BasicSwitch<Scope, Scope, Status, ? extends ResultWrapper> withStatusByScope() {
        return BasicSwitch.asBasicSwitch(withStatus().mapKey(Unsafe::getScope).mapSource(Unsafe::getScope));
    }
    
    default BasicSwitch<Exception, Exception, Status, ? extends ResultWrapper> withStatusByException() {
        return (BasicSwitch) BasicSwitch.asBasicSwitch(withStatus().mapKey(Unsafe::getException).mapSource(Unsafe::getException));
    }
    
    default Safe safe() {
        return () -> this;
    }
    
    default RuntimeExceptionWrapper<?> unchecked() {
        return new RuntimeExceptionWrapper<RuntimeException>() {
            @Override
            public NoResult failed(Exception exception) {
                return ResultWrapper.this.failed(exception).unchecked();
            }
            @Override
            public <T> ValueResult<T> value(T value) {
                return ResultWrapper.this.value(value);
            }
            @Override
            public NoResult okNoValue() {
                return ResultWrapper.this.okNoValue();
            }
            @Override
            public String toString() {
                return ResultWrapper.this.toString();
            }
        };
    }
    
    interface Safe {
        
        ResultWrapper resultWrapper();
        
        default RuntimeExceptionWrapper.Safe<?> unchecked() {
            return resultWrapper().unchecked().safe();
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, X extends Exception> Function<T, Unsafe<?, X>> consumer(CheckedConsumer<T, X> consumer) {
            return t -> {
                try {
                    consumer.accept(t);
                    return resultWrapper().okNoValue().value();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x);
                }
            };
        }

        default <T, X extends Exception> Unsafe<?, X> accept(T value, CheckedConsumer<T, X> consumer) {
            return consumer(consumer).apply(value);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U, X extends Exception> BiFunction<T, U, Unsafe<?, X>> biconsumer(CheckedBiConsumer<T, U, X> consumer) {
            return (t, u) -> {
                try {
                    consumer.accept(t, u);
                    return resultWrapper().okNoValue().value();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x);
                }
            };
        }

        default <T, U, X extends Exception> Unsafe<?, X> accept(T value1, U value2, CheckedBiConsumer<T, U, X> consumer) {
            return biconsumer(consumer).apply(value1, value2);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, R, X extends Exception> Function<T, Unsafe<R, X>> function(CheckedFunction<T, R, X> function) {
            return t -> {
                try {
                    R r = function.apply(t);
                    return resultWrapper().value(r).value();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x).asUnsafe();
                }
            };
        }

        default <T, R, X extends Exception> Unsafe<R, X> apply(T value, CheckedFunction<T, R, X> function) {
            return function(function).apply(value);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U, R, X extends Exception> BiFunction<T, U, Unsafe<R, X>> bifunction(CheckedBiFunction<T, U, R, X> function) {
            return (t, u) -> {
                try {
                    R r = function.apply(t, u);
                    return resultWrapper().value(r).value();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x).asUnsafe();
                }
            };
        }

        default <T, U, R, X extends Exception> Unsafe<R, X> apply(T value1, U value2, CheckedBiFunction<T, U, R, X> function) {
            return bifunction(function).apply(value1, value2);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, X extends Exception> Function<T, Unsafe<Boolean, X>> predicate(CheckedPredicate<T, X> predicate) {
            return t -> {
                try {
                    Boolean r = predicate.test(t);
                    return resultWrapper().value(r).value();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x).asUnsafe();
                }
            };
        }

        default <T, X extends Exception> Unsafe<Boolean, X> test(T value, CheckedPredicate<T, X> predicate) {
            return predicate(predicate).apply(value);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U, X extends Exception> BiFunction<T, U, Unsafe<Boolean, X>> bipredicate(CheckedBiPredicate<T, U, X> predicate) {
            return (t, u) -> {
                try {
                    Boolean r = predicate.test(t, u);
                    return resultWrapper().value(r).value();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x).asUnsafe();
                }
            };
        }

        default <T, U, X extends Exception> Unsafe<Boolean, X> test(T value1, U value2, CheckedBiPredicate<T, U, X> predicate) {
            return bipredicate(predicate).apply(value1, value2);
        }
        
        @SuppressWarnings("UseSpecificCatch")
        default <X extends Exception> Supplier<Unsafe<?, X>> runnable(CheckedRunnable<X> runnable) {
            return () -> {
                try {
                    runnable.run();
                    return resultWrapper().okNoValue().value();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x);
                }
            };
        }

        default <X extends Exception> Unsafe<?, X> run(CheckedRunnable<X> runnable) {
            return runnable(runnable).get();
        }
        
        @SuppressWarnings("UseSpecificCatch")
        default <T, X extends Exception> Supplier<Unsafe<T, X>> supplier(CheckedSupplier<T, X> supplier) {
            return () -> {
                try {
                    T t = supplier.get();
                    return resultWrapper().value(t).value();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x).asUnsafe();
                }
            };
        }

        default <T, X extends Exception> Unsafe<T, X> get(CheckedSupplier<T, X> supplier) {
            return supplier(supplier).get();
        }
    }
}
