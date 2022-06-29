package org.cthul.monad;

import java.util.function.Consumer;
import java.util.function.Function;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.result.*;

public interface Result<T> extends Unsafe<T, RuntimeException> {

    static <T> ValueResult<T> value(T value) {
        return new OkValue<>(value);
    }

    @Override
    default Result<T> unchecked() {
        return this;
    }

    @Override
    Result<T> ifPresent(Consumer<? super T> consumer);

    @Override
    <U> Result<U> flatMap(Function<? super T, ? extends Unsafe<U, ? extends RuntimeException>> function);

    @Override
    <U, X2 extends Exception> Result<U> map(CheckedFunction<? super T, ? extends U, X2> function) throws X2;

    @Override
    NoResult noValue() throws ValueIsPresentException;
}
