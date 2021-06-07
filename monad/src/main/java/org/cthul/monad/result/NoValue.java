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
    
    default <U> Unsafe<U, X> asUnsafe() {
        return (Unsafe) this;
    }
    
    @Override
    default <X2 extends Exception> Unsafe<Object, X2> value() throws X {
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
    default NoValue<X> ifMissing(Consumer<? super X> consumer) {
        consumer.accept(getException());
        return this;
    }

    @Override
    default <U> Unsafe<U, X> flatMap(Function<? super Object, ? extends Unsafe<U, ? extends X>> function) {
        return asUnsafe();
    }

    @Override
    default <U, X2 extends Exception> Unsafe<U, X> map(CheckedFunction<? super Object, ? extends U, X2> function) throws X2 {
        return asUnsafe();
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
