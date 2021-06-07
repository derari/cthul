package org.cthul.monad.result;

import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;

public abstract class AbstractUnsafe<T, X extends Exception> implements Unsafe<T, X> {
    
    private final Scope scope;
    private final Status status;

    public AbstractUnsafe(Scope scope, Status status) {
        this.scope = scope;
        this.status = status;
    }

    @Override
    public Scope getScope() {
        return scope;
    }

    @Override
    public Status getStatus() {
        return status;
    }
}
