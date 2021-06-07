package org.cthul.monad.result;

import org.cthul.monad.DefaultStatus;
import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.util.SafeStrings;

public class OkValue<T> extends AbstractUnsafe<T, RuntimeException> implements ValueResult<T> {
    
    protected final T value;

    public OkValue(Scope scope, T value) {
        this(scope, DefaultStatus.OK, value);
    }

    public OkValue(Scope scope, Status okStatus, T value) {
        super(scope, okStatus);
        if (!okStatus.isOk()) {
            throw new IllegalArgumentException("Value requires OK status");
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
