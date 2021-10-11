package org.cthul.monad.adapt;

import org.cthul.monad.Unsafe;
import org.cthul.monad.result.NoValue;

public interface UnsafeAdapter extends NoValueAdapter {

    @Override
    <T, X extends Exception> Unsafe<T, X> unsafe(Unsafe<T, X> unsafe);

    @Override
    default<X extends Exception> NoValue<X> noValue(NoValue<X> noValue) {
        return unsafe(noValue).noValue();
    }

    @Override
    default UnsafeAdapter asUnsafeAdapter() {
        return this;
    }
}
