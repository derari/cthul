package org.cthul.monad.result;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.Result;
import org.cthul.monad.Unsafe;
import org.cthul.monad.ValueIsPresentException;
import org.cthul.monad.function.CheckedFunction;

public interface ValueResult<T> extends Result<T> {

    @Override
    default ValueResult<T> value() {
        return this;
    }

    default <X2 extends Exception> Unsafe<T, X2> unsafe() {
        return (Unsafe) this;
    }

    @Override
    default ValueResult<T> ifPresent(Consumer<? super T> consumer) {
        consumer.accept(get());
        return this;
    }

    @Override
    default <U> Result<U> flatMap(Function<? super T, ? extends Unsafe<U, ? extends RuntimeException>> function) {
        return function.apply(get()).unchecked();
    }

    @Override
    default <X2 extends Exception> Unsafe<T, X2> flatMapException(Function<? super RuntimeException, ? extends Unsafe<? extends T, X2>> function) {
        return unsafe();
    }

    @Override
    default <U, X2 extends Exception> Result<U> map(CheckedFunction<? super T, ? extends U, X2> function) throws X2 {
        U next = function.apply(get());
        return getScope().value(getStatus(), next);
    }

    @Override
    default <X2 extends Exception, X3 extends Exception> Unsafe<T, X2> mapException(CheckedFunction<? super RuntimeException, ? extends X2, X3> function) throws X3 {
        return unsafe();
    }

    @Override
    default boolean isPresent() {
        return true;
    }

    @Override
    default RuntimeException getException() throws ValueIsPresentException {
        throw new ValueIsPresentException(this);
    }

    @Override
    default NoResult noValue() throws ValueIsPresentException {
        throw new ValueIsPresentException(this);
    }

    @Override
    default ValueResult<T> ifException(Consumer<? super RuntimeException> consumer) {
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
    default T orElseApply(Function<? super Unsafe<?, RuntimeException>, ? extends T> other) {
        return get();
    }

    @Override
    default <X2 extends Exception> T orElseThrow(Supplier<? extends X2> exceptionSupplier) throws X2 {
        return get();
    }

    @Override
    public default void logInfo(String format, Object... args) {
        args = args == null ? new Object[1] : Arrays.copyOf(args, args.length + 1);
        args[args.length - 1] = this;
        getScope().getLogger().info(format, args);
    }
}
