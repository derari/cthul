package org.cthul.monad;

public class ResultValue<T> implements UncheckedResult<T> {
    
    private final Module module;
    private final Status status;
    private final T value;

    protected ResultValue(Module module, T value) {
        this(module, DefaultStatus.OK, value);
    }

    protected ResultValue(Module module, Status status, T value) {
        this.module = module;
        this.status = status;
        this.value = value;
    }

    @Override
    public T getValue() {
        return value;
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
        return true;
    }

    @Override
    public ScopedException getException() {
        throw new IllegalArgumentException("no exception");
    }

    @Override
    public ScopedRuntimeException getRuntimeException() {
        throw new IllegalArgumentException("no exception");
    }

    @Override
    public Result<T> checked() {
        return this;
    }
}
