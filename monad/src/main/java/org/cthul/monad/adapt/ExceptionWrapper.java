package org.cthul.monad.adapt;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.*;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ValueResult;
import org.cthul.monad.switches.BasicSwitch;
import org.cthul.monad.switches.Switch;
import org.cthul.monad.util.ResultAdapter;

public interface ExceptionWrapper<X extends Exception> {

    ResultFactory<X> resultFactory();

    default RuntimeExceptionWrapper<?> unchecked() {
        return resultFactory()::unchecked;
    }

    default BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, ExceptionAdapter, ExceptionWrapper<X>> adaptResult() {
        return BasicSwitch.asBasicSwitch(resultFactory().adaptUnsafe()
                .mapResult(rf -> rf::self));
    }

    default BasicSwitch.Identity<NoValue<X>, NoValue<X>, ? extends ExceptionWrapper<X>> adaptNoValue() {
        Switch choice = adaptResult()
            .ifTrue(Unsafe::isPresent).map(u -> nv -> nv)
            .mapKey(Unsafe::noValue)
            .mapSource(Unsafe::noValue)
            .mapTargetFromSource(u -> nv -> u.noValue());
        return BasicSwitch.asIdentitySwitch(choice);
    }

    default ExceptionWrapper<X> withStatus(Status status) {
        return adaptResult().orElse().set(nv -> ResultAdapter.setStatus(nv, status).noValue());
    }

    default BasicSwitch<NoValue<X>, NoValue<X>, Status, ? extends ExceptionWrapper<X>> chooseStatus() {
        return BasicSwitch.asBasicSwitch(adaptNoValue()
            .mapTarget((u, s) -> ResultAdapter.setStatus(u, s).noValue()));
    }

    default BasicSwitch<Scope, NoValue<X>, Status, ? extends ExceptionWrapper<X>> chooseStatusByScope() {
        return chooseStatus().mapKey(Unsafe::getScope).asBasicSwitch();
    }

    default BasicSwitch<Exception, NoValue<X>, Status, ? extends ExceptionWrapper<X>> chooseStatusByException() {
        return BasicSwitch.asBasicSwitch(chooseStatus().mapKey(Unsafe::getException));
    }

    default Safe<X> safe() {
        return this::resultFactory;
    }

    default Wrap<X> wrap() {
        return this::resultFactory;
    }

    default NoValue<X> failed(Exception exception) {
        return resultFactory().failed(exception);
    }

    interface Safe<X extends Exception> {

        ResultFactory<X> exceptionWrapper();

        default <T> Function<T, Unsafe<?, X>> consumer(CheckedConsumer<T, ?> consumer) {
            return t -> accept(t, consumer);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T> Unsafe<?, X> accept(T value, CheckedConsumer<T, ?> consumer) {
            try {
                consumer.accept(value);
                return exceptionWrapper().noValue();
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
                return exceptionWrapper().noValue();
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
                return exceptionWrapper().value(r).unsafe();
            } catch (Exception x) {
                return exceptionWrapper().failed(x).unsafe();
            }
        }

        default <T, U, R> BiFunction<T, U, Unsafe<R, X>> bifunction(CheckedBiFunction<T, U, R, ?> function) {
            return (t, u) -> apply(t, u, function);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U, R> Unsafe<R, X> apply(T value1, U value2, CheckedBiFunction<T, U, R, ?> function) {
            try {
                R r = function.apply(value1, value2);
                return exceptionWrapper().value(r).unsafe();
            } catch (Exception x) {
                return exceptionWrapper().failed(x).unsafe();
            }
        }

        default <T> Function<T, Unsafe<Boolean, X>> predicate(CheckedPredicate<T, ?> predicate) {
            return t -> test(t, predicate);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T> Unsafe<Boolean, X> test(T value, CheckedPredicate<T, ?> predicate) {
            try {
                Boolean r = predicate.test(value);
                return exceptionWrapper().value(r).unsafe();
            } catch (Exception x) {
                return exceptionWrapper().failed(x).unsafe();
            }
        }

        default <T, U> BiFunction<T, U, Unsafe<Boolean, X>> bipredicate(CheckedBiPredicate<T, U, ?> predicate) {
            return (t, u) -> test(t, u, predicate);
        }

        @SuppressWarnings("UseSpecificCatch")
        default <T, U> Unsafe<Boolean, X> test(T value1, U value2, CheckedBiPredicate<T, U, ?> predicate) {
            try {
                Boolean r = predicate.test(value1, value2);
                return exceptionWrapper().value(r).unsafe();
            } catch (Exception x) {
                return exceptionWrapper().failed(x).unsafe();
            }
        }

        default Supplier<Unsafe<?, X>> runnable(CheckedRunnable<?> runnable) {
            return () -> run(runnable);
        }

        @SuppressWarnings("UseSpecificCatch")
        default Unsafe<?, X> run(CheckedRunnable<?> runnable) {
            try {
                runnable.run();
                return exceptionWrapper().noValue();
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
                return exceptionWrapper().value(t).unsafe();
            } catch (Exception x) {
                return exceptionWrapper().failed(x).unsafe();
            }
        }
    }

    interface Wrap<X extends Exception> {

        ResultFactory<X> exceptionWrapper();

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

    interface ResultFactory<X extends Exception> {

        NoValue<X> failed(Exception exception);

        <T> ValueResult<T> value(T value);

        NoValue<X> noValue();

        BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, ExceptionAdapter, ResultFactory<X>> adaptUnsafe();

        ResultFactory<RuntimeException> unchecked();

        default ResultFactory<X> self() {
            return this;
        }
    }
}
