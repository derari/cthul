package org.cthul.monad.adapt;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.Result;
import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.CheckedBiConsumer;
import org.cthul.monad.function.CheckedBiFunction;
import org.cthul.monad.function.CheckedBiPredicate;
import org.cthul.monad.function.CheckedConsumer;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.CheckedPredicate;
import org.cthul.monad.function.CheckedRunnable;
import org.cthul.monad.function.CheckedSupplier;
import org.cthul.monad.function.UncheckedBiConsumer;
import org.cthul.monad.function.UncheckedBiFunction;
import org.cthul.monad.function.UncheckedBiPredicate;
import org.cthul.monad.function.UncheckedConsumer;
import org.cthul.monad.function.UncheckedFunction;
import org.cthul.monad.function.UncheckedPredicate;
import org.cthul.monad.function.UncheckedRunnable;
import org.cthul.monad.function.UncheckedSupplier;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.switches.BasicSwitch;
import org.cthul.monad.switches.Switch;
import org.cthul.monad.util.ResultAdapter;

public interface RuntimeExceptionWrapper<X extends RuntimeException> {

    ExceptionWrapper.ResultFactory<X> resultFactory();

    default ExceptionWrapper<X> typed() {
        return resultFactory()::self;
    }

    default BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, ExceptionAdapter, RuntimeExceptionWrapper<X>> adaptUnsafe() {
        return BasicSwitch.asBasicSwitch(resultFactory().adaptUnsafe()
                .mapResult(rf -> (RuntimeExceptionWrapper) rf.unchecked()));
    }

    default BasicSwitch.Identity<NoValue<X>, NoValue<X>, ? extends RuntimeExceptionWrapper<X>> adaptNoValue() {
        Switch choice = adaptUnsafe()
                .ifTrue(Unsafe::isPresent).map(u -> nv -> nv)
                .mapKey(Unsafe::noValue)
                .mapSource(Unsafe::noValue)
                .mapTargetFromSource(u -> nv -> u.noValue());
        return BasicSwitch.asIdentitySwitch(choice);
    }

    default RuntimeExceptionWrapper<X> withStatus(Status status) {
        return adaptUnsafe().orElse().set(nv -> ResultAdapter.setStatus(nv, status).noValue());
    }

    default BasicSwitch<NoValue<X>, NoValue<X>, Status, ? extends RuntimeExceptionWrapper<X>> withStatus() {
        return BasicSwitch.asBasicSwitch(adaptNoValue()
                .mapTarget((u, s) -> ResultAdapter.setStatus(u, s).noValue()));
    }

    default BasicSwitch<Scope, NoValue<X>, Status, ? extends RuntimeExceptionWrapper<X>> withStatusByScope() {
        return withStatus().mapKey(Unsafe::getScope).asBasicSwitch();
    }

    default BasicSwitch<Exception, NoValue<X>, Status, ? extends RuntimeExceptionWrapper<X>> withStatusByException() {
        return BasicSwitch.asBasicSwitch(withStatus().mapKey(Unsafe::getException));
    }

    default Safe<X> safe() {
        return this::resultFactory;
    }

    default Wrap<X> wrap() {
        return this::resultFactory;
    }

    interface Safe<X extends RuntimeException> {

        ExceptionWrapper.ResultFactory<X> exceptionWrapper();

        @SuppressWarnings("UseSpecificCatch")
        default <T> Function<T, Result<?>> consumer(CheckedConsumer<T, ?> consumer) {
            return t -> {
                try {
                    consumer.accept(t);
                    return exceptionWrapper().value(null);
                } catch (Exception x) {
                    return exceptionWrapper().failed(x).unchecked();
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
                    return exceptionWrapper().noValue().unchecked();
                } catch (Exception x) {
                    return exceptionWrapper().failed(x).unchecked();
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
                    return exceptionWrapper().failed(x).unchecked().result();
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
                    return exceptionWrapper().failed(x).unchecked().result();
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
                    return exceptionWrapper().failed(x).unchecked().result();
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
                    return exceptionWrapper().failed(x).unchecked().result();
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
                    return exceptionWrapper().noValue().unchecked();
                } catch (Exception x) {
                    return exceptionWrapper().failed(x).unchecked();
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
                    return exceptionWrapper().failed(x).unchecked().result();
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
