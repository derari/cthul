package org.cthul.monad.error;

public interface ErrorState<This extends ErrorState<This>> {
    
    default This cast(ErrorState<?> errorState) {
        if (!getClass().isInstance(errorState)) {
            throw new ClassCastException(errorState + " can not be cast to " + getClass());
        }
        return (This) errorState;
    }
}
