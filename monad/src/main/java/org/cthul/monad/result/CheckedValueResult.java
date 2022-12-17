package org.cthul.monad.result;

import org.cthul.monad.*;
import org.cthul.monad.function.CheckedFunction;

import java.util.Arrays;
import java.util.function.*;

public interface CheckedValueResult<T, X extends Exception> extends Unsafe<T, X> {

    @Override
    T get();

    @SuppressWarnings("unchecked")
    default <X2 extends Exception> Unsafe<T, X2> unsafe() {
        return (Unsafe<T, X2>) this;
    }

    @Override
    default CheckedValueResult<T, X> ifPresent(Consumer<? super T> consumer) {
        consumer.accept(get());
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    default <U> Unsafe<U, X> flatMap(Function<? super T, ? extends Unsafe<U, ? extends X>> function) {
        return (Unsafe<U, X>) function.apply(get());
    }

    @Override
    default <X2 extends Exception> Unsafe<T, X2> flatMapException(Function<? super X, ? extends Unsafe<? extends T, X2>> function) {
        return unsafe();
    }

    @Override
    default <U, X2 extends Exception> Unsafe<U, X> map(CheckedFunction<? super T, ? extends U, X2> function) throws X2 {
        U next = function.apply(get());
        return getScope().value(getStatus(), next).unsafe();
    }

    @Override
    default <X2 extends Exception, X3 extends Exception> Unsafe<T, X2> mapException(CheckedFunction<? super X, ? extends X2, X3> function) throws X3 {
        return unsafe();
    }

    @Override
    default boolean isPresent() {
        return true;
    }

    @Override
    default X getException() throws ValueIsPresentException {
        throw new ValueIsPresentException(value());
    }

    @Override
    default NoValue<X> noValue() throws ValueIsPresentException {
        throw new ValueIsPresentException(value());
    }

    @Override
    default ValueResult<T> value() {
        return new OkValue<>(getScope(), getStatus(), get());
    }

    @Override
    default ValueResult<T> unchecked() {
        return value();
    }

    @Override
    default CheckedValueResult<T, X> ifException(Consumer<? super X> consumer) {
        return this;
    }

    @Override
    default T orElse(T other) {
        return get();
    }

    @Override
    default T orElseGet(Supplier<? extends T> other) {
        return get();
    }

    @Override
    default T orElseApply(Function<? super Unsafe<?, X>, ? extends T> other) {
        return get();
    }

    @Override
    default <X2 extends Exception> T orElseThrow(Supplier<? extends X2> exceptionSupplier) throws X2 {
        return get();
    }

    @Override
    default void logInfo(String format, Object... args) {
        args = args == null ? new Object[1] : Arrays.copyOf(args, args.length + 1);
        args[args.length - 1] = this;
        getScope().getLogger().info(format, args);
    }
}
