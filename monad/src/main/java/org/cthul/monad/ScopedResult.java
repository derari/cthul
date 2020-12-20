package org.cthul.monad;

import java.util.function.Function;
import java.util.function.Supplier;
import org.cthul.monad.Status;

public interface ScopedResult<T, X extends Exception> extends Status.Delegate {
    
    GenericScope<?> getScope();
    
    @Override
    Status getStatus();
    
    boolean hasValue();
    
    T get() throws X;
    
    X getException();
    
    default String getMessage() {
        return toString();
    }
    
    default void requireOk() throws X {
        if (isOk()) return;
        throw getException();
    }
    
    <U> U reduce(Function<? super T, ? extends U> map, Function<? super X, ? extends U> onError);

    default T or(T defaultValue) {
        return reduce(Function.identity(), x -> defaultValue);
    }

    default T orGet(Supplier<? extends T> defaultSupplier) {
        return reduce(Function.identity(), x -> defaultSupplier.get());
    }
    
    default ResultMessage asMessage() {
        return new ResultMessage(getScope().toString(), this, getMessage());
    }
}
