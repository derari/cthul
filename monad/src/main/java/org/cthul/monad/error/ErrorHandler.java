package org.cthul.monad.error;

public interface ErrorHandler {

    static ErrorHandler current() {
        return ErrorContext.CURRENT.get().getHandler();
    }
    
    <State extends ErrorState<? extends State>> State handle(State state);
    
    default ErrorContext enable() {
        return ErrorContext.enable(this);
    }
    
    default ErrorHandler with(ErrorHandlerLayer layer) {
        return new ErrorHandler() {
            @Override
            public <State extends ErrorState<? extends State>> State handle(State state) {
                return layer.handle(state, this);
            }
        };
    }
    
    static ErrorHandler DO_NOT_HANDLE = new ErrorHandler() {
        @Override
        public <State extends ErrorState<? extends State>> State handle(State state) {
            return state;
        }
        @Override
        public String toString() {
            return "NO ERROR HANDLER";
        }
    };
}
