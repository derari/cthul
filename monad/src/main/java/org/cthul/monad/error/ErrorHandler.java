package org.cthul.monad.error;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface ErrorHandler {

    static @NonNull ErrorHandler current() {
        return ErrorContext.CURRENT.get().getHandler();
    }

    static @NonNull ErrorHandler delegateCurrent() {
        return ErrorContext.DELEGATE_CURRENT;
    }

    static @NonNull ErrorHandler doNotHandle() {
        return ErrorContext.DO_NOT_HANDLE;
    }
    
    <State extends ErrorState<? extends State>> @NonNull State handle(@NonNull State state);
    
    default @NonNull ErrorContext enable() {
        return ErrorContext.enable(this);
    }
    
    default @NonNull ErrorHandler with(@NonNull ErrorHandlerLayer layer) {
        return new ErrorHandler() {
            @Override
            public <State extends ErrorState<? extends State>> State handle(State state) {
                return layer.handle(state, this);
            }
        };
    }
}
