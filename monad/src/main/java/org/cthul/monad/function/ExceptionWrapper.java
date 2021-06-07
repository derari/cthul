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
import org.cthul.monad.util.UnsafeStatusAdapter;

public interface ExceptionWrapper<X extends Exception> {
    
    default <T> Unsafe<T, X> result(T value, Exception exception) {
        if (exception == null) 
            return value(value).value();
        return failed(exception).asUnsafe();
    }
     
    NoValue<X> failed(Exception exception);
     
    <T> ValueResult<T> value(T value);
    
    NoResult okNoValue();
    
    default RuntimeExceptionWrapper<?> unchecked() {
        return new RuntimeExceptionWrapper<RuntimeException>() {
            @Override
            public NoResult failed(Exception exception) {
                return ExceptionWrapper.this.failed(exception).unchecked();
            }
            @Override
            public <T> ValueResult<T> value(T value) {
                return ExceptionWrapper.this.value(value);
            }
            @Override
            public NoResult okNoValue() {
                return ExceptionWrapper.this.okNoValue();
            }
            @Override
            public String toString() {
                return ExceptionWrapper.this.toString();
            }
        };
    }
    
    default ExceptionWrapper withStatus(Status status) {
        return withStatus().orElse().set(status);
    }
    
    default BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, Status, ? extends ExceptionWrapper<X>> withStatus() {
        return new UnsafeStatusAdapter.Checked(this).withStatus();
    }
    
    default BasicSwitch<Scope, Scope, Status, ? extends ExceptionWrapper<X>> withStatusByScope() {
        return withStatus().mapKey(Unsafe::getScope).mapSource(Unsafe::getScope).asBasicSwitch();
    }
    
    default BasicSwitch<Exception, Exception, Status, ? extends ExceptionWrapper<X>> withStatusByException() {
        return withStatus()
                .<Exception>mapKey(Unsafe::getException)
                .<Exception>mapSource(Unsafe::getException).asBasicSwitch();
    }
    
    default Safe<X> safe() {
        return () -> this;
    }
    
    default Wrap<X> wrap() {
        return () -> this;
    }
    
    interface Safe<X extends Exception> {
        
        ExceptionWrapper<X> exceptionWrapper();
        
        default RuntimeExceptionWrapper.Safe<?> unchecked() {
            return exceptionWrapper().unchecked().safe();
        }

        default <T> Function<T, Unsafe<?, X>> consumer(CheckedConsumer<T, ?> consumer) {
            return t -> accept(t, consumer);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T> Unsafe<?, X> accept(T value, CheckedConsumer<T, ?> consumer) {
            try {
                consumer.accept(value);
                return exceptionWrapper().okNoValue().value();
            } catch (Exception x) {
                return exceptionWrapper().failed(x);
            }
        }

        default <T, U> BiFunction<T, U, Unsafe<?, X>> biconsumer(CheckedBiConsumer<T, U, ?> consumer) {
            return (t, u) -> accept(t, u, consumer);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U> Unsafe<?, X> accept(T value1, U value2, CheckedBiConsumer<T, U, ?> consumer) {
            try {
                consumer.accept(value1, value2);
                return exceptionWrapper().okNoValue().value();
            } catch (Exception x) {
                return exceptionWrapper().failed(x);
            }
        }

        default <T, R> Function<T, Unsafe<R, X>> function(CheckedFunction<T, R, ?> function) {
            return t -> apply(t, function);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, R> Unsafe<R, X> apply(T value, CheckedFunction<T, R, ?> function) {
            try {
                R r = function.apply(value);
                return exceptionWrapper().value(r).value();
            } catch (Exception x) {
                return exceptionWrapper().failed(x).asUnsafe();
            }
        }

        default <T, U, R> BiFunction<T, U, Unsafe<R, X>> bifunction(CheckedBiFunction<T, U, R, ?> function) {
            return (t, u) -> apply(t, u, function);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U, R> Unsafe<R, X> apply(T value1, U value2, CheckedBiFunction<T, U, R, ?> function) {
            try {
                R r = function.apply(value1, value2);
                return exceptionWrapper().value(r).value();
            } catch (Exception x) {
                return exceptionWrapper().failed(x).asUnsafe();
            }
        }

        default <T> Function<T, Unsafe<Boolean, X>> predicate(CheckedPredicate<T, ?> predicate) {
            return t -> test(t, predicate);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T> Unsafe<Boolean, X> test(T value, CheckedPredicate<T, ?> predicate) {
            try {
                Boolean r = predicate.test(value);
                return exceptionWrapper().value(r).value();
            } catch (Exception x) {
                return exceptionWrapper().failed(x).asUnsafe();
            }
        }

        default <T, U> BiFunction<T, U, Unsafe<Boolean, X>> bipredicate(CheckedBiPredicate<T, U, ?> predicate) {
            return (t, u) -> test(t, u, predicate);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U> Unsafe<Boolean, X> test(T value1, U value2, CheckedBiPredicate<T, U, ?> predicate) {
            try {
                Boolean r = predicate.test(value1, value2);
                return exceptionWrapper().value(r).value();
            } catch (Exception x) {
                return exceptionWrapper().failed(x).asUnsafe();
            }
        }
        
        default Supplier<Unsafe<?, X>> runnable(CheckedRunnable<?> runnable) {
            return () -> run(runnable);
        }

        @SuppressWarnings("UseSpecificCatch")
        default Unsafe<?, X> run(CheckedRunnable<?> runnable) {
            try {
                runnable.run();
                return exceptionWrapper().okNoValue().value();
            } catch (Exception x) {
                return exceptionWrapper().failed(x);
            }
        }
        
        default <T> Supplier<Unsafe<T, X>> supplier(CheckedSupplier<T, ?> supplier) {
            return () -> get(supplier);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T> Unsafe<T, X> get(CheckedSupplier<T, ?> supplier) {
            try {
                T t = supplier.get();
                return exceptionWrapper().value(t).value();
            } catch (Exception x) {
                return exceptionWrapper().failed(x).asUnsafe();
            }
        }
    }
    
    interface Wrap<X extends Exception> {
        
        ExceptionWrapper<X> exceptionWrapper();
        
        default RuntimeExceptionWrapper.Wrap<?> unchecked() {
            return exceptionWrapper().unchecked().wrap();
        }

        default <T> CheckedConsumer<T, X> consumer(CheckedConsumer<T, ?> consumer) {
            return t -> accept(t, consumer);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T> void accept(T value, CheckedConsumer<T, ?> consumer) throws X {
            try {
                consumer.accept(value);
            } catch (Exception x) {
                throw exceptionWrapper().failed(x).getException();
            }
        }

        default <T, U> CheckedBiConsumer<T, U, X> biconsumer(CheckedBiConsumer<T, U, ?> consumer) {
            return (t, u) -> accept(t, u, consumer);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U> void accept(T value1, U value2, CheckedBiConsumer<T, U, ?> consumer) throws X {
            try {
                consumer.accept(value1, value2);
            } catch (Exception x) {
                throw exceptionWrapper().failed(x).getException();
            }
        }

        default <T, R> CheckedFunction<T, R, X> function(CheckedFunction<T, R, ?> function) {
            return t -> apply(t, function);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, R> R apply(T value, CheckedFunction<T, R, ?> function) throws X {
            try {
                return function.apply(value);
            } catch (Exception x) {
                throw exceptionWrapper().failed(x).getException();
            }
        }

        default <T, U, R> CheckedBiFunction<T, U, R, X> bifunction(CheckedBiFunction<T, U, R, ?> function) {
            return (t, u) -> apply(t, u, function);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U, R> R apply(T value1, U value2, CheckedBiFunction<T, U, R, ?> function) throws X {
            try {
                return function.apply(value1, value2);
            } catch (Exception x) {
                throw exceptionWrapper().failed(x).getException();
            }
        }

        default <T> CheckedPredicate<T, X> predicate(CheckedPredicate<T, ?> predicate) {
            return t -> test(t, predicate);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T> boolean test(T value, CheckedPredicate<T, ?> predicate) throws X {
            try {
                return predicate.test(value);
            } catch (Exception x) {
                throw exceptionWrapper().failed(x).getException();
            }
        }

        default <T, U> CheckedBiPredicate<T, U, X> bipredicate(CheckedBiPredicate<T, U, ?> predicate) {
            return (t, u) -> test(t, u, predicate);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U> boolean test(T value1, U value2, CheckedBiPredicate<T, U, ?> predicate) throws X {
            try {
                return predicate.test(value1, value2);
            } catch (Exception x) {
                throw exceptionWrapper().failed(x).getException();
            }
        }
        
        default CheckedRunnable<X> runnable(CheckedRunnable<?> runnable) {
            return () -> run(runnable);
        }

        @SuppressWarnings("UseSpecificCatch")
        default void run(CheckedRunnable<?> runnable) throws X {
            try {
                runnable.run();
            } catch (Exception x) {
                throw exceptionWrapper().failed(x).getException();
            }
        }
        
        default <T> CheckedSupplier<T, X> supplier(CheckedSupplier<T, ?> supplier) {
            return () -> get(supplier);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T> T get(CheckedSupplier<T, ?> supplier) throws X {
            try {
                return supplier.get();
            } catch (Exception x) {
                throw exceptionWrapper().failed(x).getException();
            }
        }
    }
}
