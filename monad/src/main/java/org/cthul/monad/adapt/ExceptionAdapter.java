package org.cthul.monad.adapt;

import org.cthul.monad.Unsafe;
import org.cthul.monad.result.NoValue;

public interface ExceptionAdapter {

    NoValue<?> noValueUntyped(NoValue<?> noValue);

    default <T, X extends Exception> Unsafe<T, ?> unsafe(Unsafe<T, X> unsafe) {
        if (unsafe.isPresent()) {
            return unsafe;
        }
        return noValueUntyped(unsafe.noValue()).unsafe();
    }
}
