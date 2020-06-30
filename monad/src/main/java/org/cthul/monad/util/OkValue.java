package org.cthul.monad.util;

import java.util.function.Function;
import org.cthul.monad.*;
import org.cthul.monad.cache.Cached;
import org.cthul.monad.cache.CachedMeta;

public class OkValue<T> extends GenericOkValue<T, ScopedException> implements Cached<T>, CachedMeta.Delegator {
    
    private static final CachedMeta NO_STORE = CachedMeta.noStore();
    
    private Unchecked unchecked = null;
    private CachedMeta cachedMeta;

    public OkValue(Scope module, T value) {
        this(module, DefaultStatus.OK, value);
    }

    public OkValue(Scope module, Status okStatus, T value) {
        super(module, okStatus, value);
        this.cachedMeta = NO_STORE;
    }

    @Override
    public CachedMeta getCachedMeta() {
        return cachedMeta;
    }

    @Override
    public Cached<T> cacheControl(CachedMeta meta) {
        this.cachedMeta = meta;
        return this;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public Scope getScope() {
        return (Scope) scope;
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
    public Result.Unchecked<T> unchecked() {
        if (unchecked == null) {
            unchecked = new Unchecked();
        }
        return unchecked;
    }

    @Override
    public ScopedException getException() {
        throw new IllegalArgumentException("no exception");
    }

    @Override
    public void requireOk() {
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
    
    private class Unchecked implements Result.Unchecked<T>, Status.Delegate {

        @Override
        public Result<T> checked() {
            return OkValue.this;
        }

        @Override
        public boolean hasValue() {
            return true;
        }

        @Override
        public T get() {
            return OkValue.this.get();
        }

        @Override
        public <U> U reduce(Function<? super T, ? extends U> map, Function<? super ScopedRuntimeException, ? extends U> onError) {
            return map.apply(get());
        }

        @Override
        public ScopedRuntimeException getException() {
            throw new IllegalArgumentException("no exception");
        }
    }
}
