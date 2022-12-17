package org.cthul.monad.result;

import org.cthul.monad.*;
import org.cthul.monad.util.SafeStrings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractOkValue<T, X extends Exception> extends AbstractUnsafe<T, X> implements CheckedValueResult<T, X> {

    protected final T value;

    public AbstractOkValue(Scope scope, T value) {
        this(scope, DefaultStatus.OK, value);
    }

    public AbstractOkValue(Scope scope, Status okStatus, T value) {
        super(scope, okStatus);
        if (!okStatus.isOk()) {
            throw scope.uncheckedException().internal("Value requires OK status");
        }
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public String toString() {
        return SafeStrings.toString(value);
    }
}
