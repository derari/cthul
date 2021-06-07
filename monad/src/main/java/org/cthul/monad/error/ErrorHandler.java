package org.cthul.monad.error;

public interface ErrorHandler {

    static ErrorHandler getCurrent() {
        return ErrorHandlingScope.CURRENT.get().getHandler();
    }
    
    static ErrorHandler useCurrent() {
        return ErrorHandlingScope.USE_CURRENT;
    }
    
    static ErrorHandler doNothing() {
        return ErrorHandlingScope.DO_NOT_HANDLE;
    }
    
    <State extends ErrorState<? extends State>> State handle(State state);
    
    default ErrorHandlingScope enable() {
        return ErrorHandlingScope.enable(this);
    }
    
    default ErrorHandler with(ErrorHandlerLayer layer) {
        return new ErrorHandler() {
            @Override
            public <State extends ErrorState<? extends State>> State handle(State state) {
                return layer.handle(state, this);
            }
            @Override
            public String toString() {
                return layer + "|" + ErrorHandler.this;
            }
        };
    }
}
