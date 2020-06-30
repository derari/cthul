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
        this.handler = ErrorHandler.DO_NOT_HANDLE;
        this.previous = null;
    }
    
    protected ErrorContext(ErrorHandler handler) {
        this(handler, CURRENT.get());
    }

    protected ErrorContext(ErrorHandler handler, ErrorContext previous) {
        this.handler = handler;
        this.previous = previous;
    }
    
    public ErrorContext enable() {
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
