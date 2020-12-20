package org.cthul.monad.util;

import org.cthul.monad.GenericScope;
import org.cthul.monad.ScopedResult;
import java.util.function.Function;
import org.cthul.monad.DefaultStatus;
import org.cthul.monad.Status;

public class GenericOkValue<T, X extends Exception> implements ScopedResult<T, X> {
    
    protected final GenericScope<?> scope;
    protected final Status status;
    protected final T value;

    public GenericOkValue(GenericScope<?> scope, T value) {
        this(scope, DefaultStatus.OK, value);
    }

    public GenericOkValue(GenericScope<?> scope, Status okStatus, T value) {
        if (!okStatus.isOk()) {
            throw new IllegalArgumentException("Value requires OK status");
        }
        this.scope = scope;
        this.status = okStatus;
        this.value = value;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public GenericScope<?> getScope() {
        return scope;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean hasValue() {
        return true;
    }

    @Override
    public X getException() {
        throw new IllegalArgumentException("no exception");
    }

    @Override
    public void requireOk() {
    }

    @Override
    public <U> U reduce(Function<? super T, ? extends U> map, Function<? super X, ? extends U> onError) {
        return map.apply(get());
    }

    @Override
    public String getMessage() {
        return toString();
    }

    @Override
    public String toString() {
        try {
            return String.valueOf(value);
        } catch (RuntimeException ex) {
            return ex.getClass().getSimpleName() + ": " + ex.getMessage();
        }
    }
}
