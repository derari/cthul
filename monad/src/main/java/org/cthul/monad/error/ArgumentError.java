package org.cthul.monad.error;

import java.util.function.Predicate;
import java.util.function.Supplier;
import org.cthul.monad.DefaultStatus;
import org.cthul.monad.util.GenericScope;
import org.cthul.monad.util.ScopedResult;

public class ArgumentError<T, X extends Exception> extends AbstractErrorState<ArgumentError<T, X>, X> {
    
    private final GenericScope<? extends X> scope;
    private final Object context;
    private final String operation;
    private final String parameter;
    private final Object value;
    private final String error;
    private final Class<T> expected;
    private T resolvedValue = null;
    private boolean resolved = false;

    public ArgumentError(Object context, String operation, String parameter, Class<T> expected, Object value, ScopedResult<?, ? extends X> result) {
        super(result);
        this.scope = null;
        this.context = context;
        this.operation = operation;
        this.parameter = parameter;
        this.expected = expected;
        this.value = value;
        this.error = result.getMessage();
    }

    public ArgumentError(Object context, String operation, String parameter, Class<T> expected, Object value, String error, X exception) {
        super(exception);
        this.scope = null;
        this.context = context;
        this.operation = operation;
        this.parameter = parameter;
        this.expected = expected;
        this.value = value;
        this.error = error;
    }

    public ArgumentError(Object context, String operation, String parameter, Class<T> expected, Object value, String error, Supplier<? extends X> exceptionSource) {
        super(exceptionSource);
        this.scope = null;
        this.context = context;
        this.operation = operation;
        this.parameter = parameter;
        this.expected = expected;
        this.value = value;
        this.error = error;
    }

    protected ArgumentError(ArgumentError<T, ? extends X> source) {
        super(source);
        this.scope = source.scope;
        this.context = source.context;
        this.operation = source.operation;
        this.parameter = source.parameter;
        this.value = source.value;
        this.error = source.error;
        this.expected = source.expected;
        this.resolved = source.resolved;
        this.resolvedValue = source.resolvedValue;
    }

    @Override
    protected X createException() {
        if (scope != null) {
            String type = expected == null ? "" : (expected.getSimpleName() + " ");
            return scope.exception(DefaultStatus.UNPROCESSABLE, "%s #%s (%s%s = %s): %s", context, operation, type, parameter, value, error);
        }
        return super.createException();
    }

    public GenericScope<? extends X> getScope() {
        return scope;
    }

    public Object getContext() {
        return context;
    }

    public String getOperation() {
        return operation;
    }

    public String getParameter() {
        return parameter;
    }

    public Class<T> getExpectedType() {
        return expected;
    }

    public Object getValue() {
        return value;
    }

    public String getError() {
        return error;
    }

    public boolean isResolved() {
        return resolved;
    }

    public T getResolved() throws X {
        if (isResolved()) return peekResolvedValue();
        throw getException();
    }

    public T getResolved(Predicate<? super T> condition) throws X {
        T newValue = getResolved();
        if (!condition.test(newValue)) {
            throw getException();
        }
        return newValue;
    }
    
    public T peekResolvedValue() {
        return resolvedValue;
    }

    public void setResolvedValue(T resolvedValue) {
        if (expected != null) {
            resolvedValue = expected.cast(resolvedValue);
        }
        resolved = true;
        this.resolvedValue = resolvedValue;
    }
    
    public ArgumentError<T, X> resolve(T resolvedValue) {
        setResolvedValue(resolvedValue);
        return this;
    }

    @Override
    public ArgumentError<T, X> cast(ErrorState<?> errorState) {
        if (errorState instanceof ArgumentError) {
            return cast((ArgumentError) errorState);
        }
        throw new ClassCastException(errorState + " can not be cast to " + getClass());
    }

    protected ArgumentError<T, X> cast(ArgumentError<?, ?> errorState) {
        if (matchTargetType(errorState)) {
            return (ArgumentError) errorState;
        }
        T newResolved = getAsTargetType(errorState);
        ArgumentError<T, X> copy = new ArgumentError<>(this);
        copy.setResolvedValue(newResolved);
        return copy;
    }
    
    protected boolean matchTargetType(ArgumentError<?, ?> errorState) {
        Class<T> type = getExpectedType();
        Object target = errorState.peekResolvedValue();
        return !errorState.isResolved() ||
                type == null || target == null ||
                type.isInstance(target);
    }
    
    protected T getAsTargetType(ArgumentError<?, ?> errorState) {
        Class<T> type = getExpectedType();
        Object target = errorState.peekResolvedValue();
        return type == null ? (T) target : type.cast(target);
    }
}
