package org.cthul.monad.adapt;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.*;
import org.cthul.monad.function.*;
import org.cthul.monad.result.NoResult;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ValueResult;
import org.cthul.monad.switches.BasicSwitch;
import org.cthul.monad.switches.Switch;
import org.cthul.monad.util.ResultAdapter;

public interface ResultWrapper extends UnsafeAdapter, ExceptionAdapter {

    default ResultWrapper withStatus(Status status) {
        return chooseStatus().orElse().set(status);
    }

    BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, UnsafeAdapter, ? extends ResultWrapper> adaptResult();

    default BasicSwitch<NoValue<?>, NoValue<?>, NoValueAdapter, ? extends ResultWrapper> adaptNoValue() {
        Switch choice = adaptResult()
                .ifTrue(Unsafe::isPresent).set(ResultWrapper.keep())
                .mapKey(Unsafe::noValue)
                .mapSource(Unsafe::noValue)
                .mapTarget(NoValueAdapter::asUnsafeAdapter);
        return choice.asBasicSwitch();
    }

    default BasicSwitch<NoValue<?>, NoValue<?>, Status, ? extends ResultWrapper> chooseStatus() {
        return adaptNoValue().<Status>mapTarget(status -> setStatus(status)).asBasicSwitch();
    }

    default BasicSwitch<Scope, NoValue<?>, Status, ? extends ResultWrapper> chooseStatusByScope() {
        return chooseStatus().mapKey(Unsafe::getScope).asBasicSwitch();
    }

    default BasicSwitch<Exception, NoValue<?>, Status, ? extends ResultWrapper> chooseStatusByException() {
        return (BasicSwitch) chooseStatus().mapKey(Unsafe::getException).asBasicSwitch();
    }

    default Safe safe() {
        return () -> this;
    }

    default Wrap wrap() {
        return () -> this;
    }

    <X extends Exception> NoValue<X> failed(X exception);

    default <T> ValueResult<T> value(T value) {
        return value(DefaultStatus.OK, value);
    }

    <T> ValueResult<T> value(Status status, T value);

    @Override
    <T, X extends Exception> Unsafe<T, X> unsafe(Unsafe<T, X> unsafe);

    default <T> ValueResult<T> valueResult(ValueResult<T> value) {
        return unsafe(value).value();
    }

    @Override
    default <X extends Exception> NoValue<X> noValue(NoValue<X> noValue) {
        return unsafe(noValue).noValue();
    }

    @Override
    default NoValue<?> noValueUntyped(NoValue<?> noValue) {
        return noValue(noValue);
    }

    default <T> Result<T> result(Result<T> result) {
        return unsafe(result).unchecked();
    }

    default NoResult noResult(NoResult noResult) {
        return unsafe(noResult).noValue().unchecked();
    }

    interface Safe {

        ResultWrapper resultWrapper();

        @SuppressWarnings("UseSpecificCatch")
        default <T, X extends Exception> Function<T, Unsafe<?, X>> consumer(CheckedConsumer<T, X> consumer) {
            return t -> {
                try {
                    consumer.accept(t);
                    return resultWrapper().value(DefaultStatus.NO_VALUE, null).unsafe();
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
                    return resultWrapper().value(DefaultStatus.NO_VALUE, null).unsafe();
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
                    return resultWrapper().value(r).unsafe();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x).unsafe();
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
                    return resultWrapper().value(r).unsafe();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x).unsafe();
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
                    return resultWrapper().value(r).unsafe();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x).unsafe();
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
                    return resultWrapper().value(r).unsafe();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x).unsafe();
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
                    return resultWrapper().value(DefaultStatus.NO_VALUE, null).unsafe();
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
                    return resultWrapper().value(t).unsafe();
                } catch (Exception x) {
                    return resultWrapper().failed((X) x).unsafe();
                }
            };
        }

        default <T, X extends Exception> Unsafe<T, X> get(CheckedSupplier<T, X> supplier) {
            return supplier(supplier).get();
        }
    }

    interface Wrap {

        ResultWrapper resultWrapper();

        default <T, R extends Unsafe<?,?>> Function<T, R> function(Function<T, R> function) {
            return t -> (R) resultWrapper().unsafe((Unsafe) function.apply(t));
        }

        default <T, R extends Unsafe<?,?>> R apply(T value, Function<T, R> function) {
            return function(function).apply(value);
        }

        default <T, U, R extends Unsafe<?,?>> BiFunction<T, U, R> bifunction(BiFunction<T, U, R> function) {
            return (t, u) -> (R) resultWrapper().unsafe((Unsafe) function.apply(t, u));
        }

        default <T, U, R extends Unsafe<?,?>> R apply(T value1, U value2, BiFunction<T, U, R> function) {
            return bifunction(function).apply(value1, value2);
        }

        default <R extends Unsafe<?,?>> Supplier<R> supplier(Supplier<R> supplier) {
            return () -> (R) resultWrapper().unsafe((Unsafe) supplier.get());
        }

        default <R extends Unsafe<?,?>> R get(Supplier<R> supplier) {
            return supplier(supplier).get();
        }
    }

    static UnsafeAdapter setStatus(Status status) {
        return setStatus(u -> status);
    }

    @SuppressWarnings("Convert2Lambda")
    static UnsafeAdapter setStatus(Function<? super Unsafe<?,?>, ? extends Status> mapping) {
        return new UnsafeAdapter() {
            @Override
            public <T, X extends Exception> Unsafe<T, X> unsafe(Unsafe<T, X> unsafe) {
                Status newStatus = mapping.apply(unsafe);
                return ResultAdapter.setStatus(unsafe, newStatus);
            }
        };
    }

    @SuppressWarnings("Convert2Lambda")
    static UnsafeAdapter keep() {
        return new UnsafeAdapter() {
            @Override
            public <T, X extends Exception> Unsafe<T, X> unsafe(Unsafe<T, X> unsafe) {
                return unsafe;
            }
        };
    }
}
