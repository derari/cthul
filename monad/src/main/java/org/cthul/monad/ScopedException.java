package org.cthul.monad;

public class ScopedException extends Exception implements Result<Object> {
    
    private final Module module;
    private final Status status;
    private ScopedRuntimeException runtimeException;

    protected ScopedException(Module module, Status status, String message) {
        super(message);
        this.module = module;
        this.status = status;
    }

    protected ScopedException(Module module, Status status, String message, Throwable cause) {
        super(message, cause);
        this.module = module;
        this.status = status;
    }

    protected ScopedException(Module module, Status status, Throwable cause) {
        super(cause);
        this.module = module;
        this.status = status;
    }

    protected ScopedException(Module module, Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.module = module;
        this.status = status;
    }

    protected ScopedException(ScopedRuntimeException runtimeException) {
        super(runtimeException.getMessage(), runtimeException);
        this.runtimeException = runtimeException;
        this.module = runtimeException.getModule();
        this.status = runtimeException.getStatus();
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public Object getValue() throws ScopedException {
        throw getException();
    }

    @Override
    public ScopedException getException() {
        return this;
    }

    @Override
    public ScopedRuntimeException getRuntimeException() {
        if (runtimeException == null) {
            runtimeException = new ScopedRuntimeException(this);
        }
        return runtimeException;
    }

    @Override
    public ScopedRuntimeException unchecked() {
        return getRuntimeException();
    }
}
