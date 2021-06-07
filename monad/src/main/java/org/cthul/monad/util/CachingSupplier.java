package org.cthul.monad.util;

import java.util.function.Supplier;

public class CachingSupplier<T> implements Supplier<T> {

    public static <T> CachingSupplier<T> cache(Supplier<T> supplier) {
        return new CachingSupplier<>(supplier);
    }
    
    private Supplier<T> supplier;
    private T value = null;
    private RuntimeException exception = null;

    public CachingSupplier(Supplier<T> supplier) {
        this.supplier = supplier;
    }
    
    @Override
    public T get() {
        if (exception != null) {
            throw exception;
        }
        Supplier<T> tmp = supplier;
        if (tmp == null) {
            return value;
        }
        try {
            return value = tmp.get();
        } catch (RuntimeException ex) {
            throw exception = ex;
        } finally {
            supplier = null;
        }
    }
}
