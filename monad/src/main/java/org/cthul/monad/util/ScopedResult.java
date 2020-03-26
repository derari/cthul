package org.cthul.monad.util;

import java.util.function.Supplier;
import org.cthul.monad.*;

public interface ScopedResult<T> extends Status.Delegate {
    
    Module getModule();
    
    @Override
    Status getStatus();
    
    boolean hasValue();
    
    ScopedException getException();
    
    ScopedRuntimeException getRuntimeException();
    
    Result.Unchecked<T> unchecked();

    default T or(T defaultValue) {
        if (!hasValue()) {
            return defaultValue;
        }
        return unchecked().getValue();
    }

    default T orGet(Supplier<? extends T> defaultSupplier) {
        if (!hasValue()) {
            return defaultSupplier.get();
        }
        return unchecked().getValue();
    }
}
