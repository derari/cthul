package org.cthul.monad.error;

public class ErrorContext implements AutoCloseable, ErrorHandler {
    
    public static ErrorContext enable(ErrorHandlerLayer layer) {
        ErrorContext previous = CURRENT.get();
        ErrorHandler handler = previous.with(layer);
        return new ErrorContext(handler, previous).enable();
    }
    
    public static ErrorContext enable(ErrorHandler handler) {
        ErrorContext previous = CURRENT.get();
        return new ErrorContext(handler, previous).enable();
    }
    
    private final ErrorHandler handler;
    private final ErrorContext previous;
    private boolean closed = false;
    
    private ErrorContext() {
        this.handler = DO_NOT_HANDLE;
        this.previous = null;
    }
    
    protected ErrorContext(ErrorHandler handler) {
        this(handler, CURRENT.get());
    }

    protected ErrorContext(ErrorHandler handler, ErrorContext previous) {
        this.handler = handler;
        this.previous = previous;
    }
    
    @Override
    public ErrorContext enable() {
        closed = false;
        CURRENT.set(this);
        return this;
    }
    
    protected ErrorHandler getHandler() {
        return handler;
    }

    @Override
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
    
    static ErrorHandler DELEGATE_CURRENT = new ErrorHandler() {
        @Override
        public <State extends ErrorState<? extends State>> State handle(State state) {
            return CURRENT.get().getHandler().handle(state);
        }

        @Override
        public ErrorHandler with(ErrorHandlerLayer layer) {
            return layer.withCurrentAsParent();
        }

        @Override
        public ErrorContext enable() {
            return CURRENT.get().getHandler().enable();
        }

        @Override
        public String toString() {
            return "Delegate to current(" + CURRENT.get().getHandler() + ")";
        }
    };
    
    @SuppressWarnings("Convert2Lambda")
    public static final ErrorContext ROOT = new ErrorContext() {
        @Override
        public void close() {
        }
        @Override
        public String toString() {
            return "ROOT";
        }
    };

    static final ThreadLocal<ErrorContext> CURRENT = ThreadLocal.withInitial(() -> ROOT);
}
