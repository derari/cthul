package org.cthul.monad.result;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.Result;
import org.cthul.monad.Unsafe;
import org.cthul.monad.ValueIsPresentException;
import org.cthul.monad.function.CheckedFunction;

public interface ValueResult<T> extends Result<T>, CheckedValueResult<T, RuntimeException> {

    @Override
    default ValueResult<T> value() {
        return this;
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
    default <U, X2 extends Exception> Result<U> map(CheckedFunction<? super T, ? extends U, X2> function) throws X2 {
        U next = function.apply(get());
        return getScope().value(getStatus(), next);
    }

    @Override
    default NoResult noValue() throws ValueIsPresentException {
        throw new ValueIsPresentException(value());
    }

    @Override
    default ValueResult<T> unchecked() {
        return this;
    }
}
