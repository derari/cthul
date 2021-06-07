package org.cthul.monad.error;

import org.cthul.monad.function.UncheckedAutoClosable;

public class ErrorHandlingScope implements UncheckedAutoClosable, ErrorHandler {
    
    public static ErrorHandlingScope enable(ErrorHandlerLayer layer) {
        ErrorHandlingScope previous = CURRENT.get();
        ErrorHandler handler = previous.with(layer);
        return new ErrorHandlingScope(handler, previous).enable();
    }
    
    public static ErrorHandlingScope enable(ErrorHandler handler) {
        ErrorHandlingScope previous = CURRENT.get();
        return new ErrorHandlingScope(handler, previous).enable();
    }
    
    private final ErrorHandler handler;
    private final ErrorHandlingScope previous;
    private boolean closed = false;
    
    private ErrorHandlingScope() {
        this.handler = DO_NOT_HANDLE;
        this.previous = null;
    }
    
    protected ErrorHandlingScope(ErrorHandler handler) {
        this(handler, CURRENT.get());
    }

    protected ErrorHandlingScope(ErrorHandler handler, ErrorHandlingScope previous) {
        if (handler == null) throw new NullPointerException("handler");
        if (previous == null) throw new NullPointerException("previous");
        this.handler = handler;
        this.previous = previous;
    }
    
    public ErrorHandlingScope enable() {
        closed = false;
        CURRENT.set(this);
        return this;
    }
    
    protected ErrorHandler getHandler() {
        return handler;
    }

    public <State extends ErrorState<? extends State>> State handle(State state) {
        return getHandler().handle(state);
    }

    @Override
    public ErrorHandler with(ErrorHandlerLayer layer) {
        return getHandler().with(layer);
    }

    @Override
    public void close() {
        closed = true;
        previous.backToTop();
    }
    
    private void backToTop() {
        if (closed) {
            previous.backToTop();
        } else {
            CURRENT.set(this);
        }
    }
    
    static final DoNotHandle DO_NOT_HANDLE = new DoNotHandle();
    
    static final UseCurrentHandler USE_CURRENT = new UseCurrentHandler();
    
    @SuppressWarnings("Convert2Lambda")
    public static final ErrorHandlingScope ROOT = new ErrorHandlingScope() {
        @Override
        public void close() {
        }
        @Override
        public String toString() {
            return "ROOT";
        }
    };

    static final ThreadLocal<ErrorHandlingScope> CURRENT = ThreadLocal.withInitial(() -> ROOT);
    
    static class DoNotHandle implements ErrorHandler, ErrorHandlerLayer {
        @Override
        public <State extends ErrorState<? extends State>> State handle(State state) {
            return state;
        }

        @Override
        public <State extends ErrorState<? extends State>> State handle(State state, ErrorHandler parent) {
            return state;
        }

        @Override
        public String toString() {
            return "NO ERROR HANDLER";
        }

        @Override
        public ErrorHandlingScope enable() {
            return ErrorHandler.super.enable();
        }
    }
    
    static class UseCurrentHandler implements ErrorHandler {

        private final ThreadLocal<Boolean> guard = ThreadLocal.withInitial(() -> false);
        
        @Override
        public <State extends ErrorState<? extends State>> State handle(State state) {
            if (guard.get()) return state;
            try {
                guard.set(true);
                return CURRENT.get().handle(state);
            } finally {
                guard.set(false);
            }
        }

        @Override
        public String toString() {
            return "USE CURRENT";
        }
    }
}
