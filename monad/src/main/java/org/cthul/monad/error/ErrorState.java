package org.cthul.monad.error;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface ErrorState<This extends ErrorState<This>> {
    
    default @NonNull This cast(@NonNull ErrorState<?> errorState) {
        if (!getClass().isInstance(errorState)) {
            throw new ClassCastException(errorState + " can not be cast to " + getClass());
        }
        return (This) errorState;
    }
}
