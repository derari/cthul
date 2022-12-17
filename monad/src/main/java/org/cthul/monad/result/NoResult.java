package org.cthul.monad.result;

import java.util.function.Consumer;
import java.util.function.Function;
import org.cthul.monad.Result;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.CheckedFunction;

public interface NoResult extends NoValue<RuntimeException>, Result<Object> {

    @Override
    default NoResult noValue() {
        return this;
    }

    @SuppressWarnings("unchecked")
    default <U> Result<U> result() {
        return (Result<U>) this;
    }

    @Override
    default NoResult unchecked() {
        return this;
    }

    @Override
    default NoResult ifPresent(Consumer<? super Object> consumer) {
        return this;
    }

    @Override
    default NoResult ifException(Consumer<? super RuntimeException> consumer) {
        consumer.accept(getException());
        return this;
    }

    @Override
    default <U> Result<U> flatMap(Function<? super Object, ? extends Unsafe<U, ? extends RuntimeException>> function) {
        return result();
    }

    @Override
    default <U, X2 extends Exception> Result<U> map(CheckedFunction<? super Object, ? extends U, X2> function) throws X2 {
        return result();
    }
}
