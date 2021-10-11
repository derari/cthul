package org.cthul.monad.result;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.ScopedRuntimeException;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.util.CachingSupplier;

public interface NoValue<X extends Exception> extends Unsafe<Object, X> {

    @Override
    default boolean isPresent() {
        return false;
    }

    @Override
    default Object get() throws X {
        throw getException();
    }

    @Override
    default NoValue<X> noValue() {
        return this;
    }

    default <U> Unsafe<U, X> unsafe() {
        return (Unsafe) this;
    }

    @Override
    default ValueResult<Object> value() throws X {
        throw getException();
    }

    @Override
    public default NoResult unchecked() {
        Supplier<RuntimeException> wrapper = () -> ScopedRuntimeException.withScope(getScope()).exception(getStatus(), getException());
        return new RuntimeExceptionResult(getScope(), getStatus(), CachingSupplier.cache(wrapper));
    }

    @Override
    default NoValue<X> ifPresent(Consumer<? super Object> consumer) {
        return this;
    }

    @Override
    default NoValue<X> ifException(Consumer<? super X> consumer) {
        consumer.accept(getException());
        return this;
    }

    @Override
    default <U> Unsafe<U, X> flatMap(Function<? super Object, ? extends Unsafe<U, ? extends X>> function) {
        return unsafe();
    }

    @Override
    default <X2 extends Exception> Unsafe<Object, X2> flatMapException(Function<? super X, ? extends Unsafe<? extends Object, X2>> function) {
        return (Unsafe) function.apply(getException());
    }

    @Override
    default <U, X2 extends Exception> Unsafe<U, X> map(CheckedFunction<? super Object, ? extends U, X2> function) throws X2 {
        return unsafe();
    }

    @Override
    default <X2 extends Exception, X3 extends Exception> Unsafe<Object, X2> mapException(CheckedFunction<? super X, ? extends X2, X3> function) throws X3 {
        X2 newException = function.apply(getException());
        return getScope().failed(getStatus(), newException);
    }

    @Override
    default Object orElse(Object other) {
        return other;
    }

    @Override
    default Object orElseGet(Supplier<? extends Object> other) {
        return other.get();
    }

    @Override
    default Object orElseApply(Function<? super Unsafe<?, X>, ? extends Object> other) {
        return other.apply(this);
    }

    @Override
    default <X2 extends Exception> Object orElseThrow(Supplier<? extends X2> exceptionSupplier) throws X2 {
        throw exceptionSupplier.get();
    }

    @Override
    default void logInfo(String format, Object... args) {
        args = args == null ? new Object[2] : Arrays.copyOf(args, args.length + 2);
        args[args.length - 2] = this;
        args[args.length - 1] = getException();
        getScope().getLogger().info(format, args);
    }
}
