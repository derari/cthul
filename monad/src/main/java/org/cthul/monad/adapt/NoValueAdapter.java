package org.cthul.monad.adapt;

import org.cthul.monad.Unsafe;
import org.cthul.monad.result.NoValue;

public interface NoValueAdapter extends ExceptionAdapter {

    <X extends Exception> NoValue<X> noValue(NoValue<X> noValue);

    @Override
    default NoValue<?> noValueUntyped(NoValue<?> noValue) {
        return noValue(noValue);
    }

    @SuppressWarnings(value = "Convert2Lambda")
    default UnsafeAdapter asUnsafeAdapter() {
        return new UnsafeAdapter() {
            @Override
            public <T, X extends Exception> Unsafe<T, X> unsafe(Unsafe<T, X> unsafe) {
                if (unsafe.isPresent()) {
                    return unsafe;
                }
                return NoValueAdapter.this.noValue(unsafe.noValue()).unsafe();
            }
        };
    }
}
