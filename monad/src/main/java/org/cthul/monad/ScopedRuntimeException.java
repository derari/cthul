package org.cthul.monad;

public class ScopedRuntimeException extends RuntimeException implements Result.Unchecked<Object> {
    
    private final Module module;
    private final Status status;
    private ScopedException checkedException;

    protected ScopedRuntimeException(Module module, Status status, String message) {
        super(message);
        this.module = module;
        this.status = status;
    }

    protected ScopedRuntimeException(Module module, Status status, String message, Throwable cause) {
        super(message, cause);
        this.module = module;
        this.status = status;
    }

    protected ScopedRuntimeException(Module module, Status status, Throwable cause) {
        super(cause);
        this.module = module;
        this.status = status;
    }

    protected ScopedRuntimeException(Module module, Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.module = module;
        this.status = status;
    }
    
    protected ScopedRuntimeException(ScopedException checkedException) {
        super(checkedException.getMessage(), checkedException);
        this.checkedException = checkedException;
        this.module = checkedException.getModule();
        this.status = checkedException.getStatus();
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
    public Object getValue() {
        throw getRuntimeException();
    }

    @Override
    public ScopedException getException() {
        if (checkedException == null) {
            checkedException = new ScopedException(this);
        }
        return checkedException;
    }

    @Override
    public ScopedRuntimeException getRuntimeException() {
        return this;
    }
    
    @Override
    public ScopedException checked() {
        return getException();
    }
}
