package org.cthul.monad.error;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.cthul.monad.util.CMObjects;
import org.cthul.monad.ScopedResult;

public class ArgumentError<T, X extends Exception> extends GeneralErrorState<ArgumentError<T, X>, X> {
    
    private final @NonNull Operation operation;
    private final @NonNull Parameter<T> parameter;
    private final @Nullable Object value;
    private T resolvedValue = null;
    private boolean resolved = false;

    public ArgumentError(@NonNull Operation operation, @NonNull Parameter<T> parameter, Object value, @NonNull ScopedResult<?, ? extends X> result) {
        super(result);
        this.operation = operation;
        this.parameter = parameter;
        this.value = value;
    }

    public ArgumentError(@NonNull Operation operation, @NonNull Parameter<T> parameter, Object value, @NonNull X exception) {
        super(exception);
        this.operation = operation;
        this.parameter = parameter;
        this.value = value;
    }

    public ArgumentError(@NonNull Operation operation, @NonNull Parameter<T> parameter, Object value, @NonNull Function<? super ErrorState<?>, ? extends X> exceptionSource, @NonNull String message) {
        super(exceptionSource, message);
        this.operation = operation;
        this.parameter = parameter;
        this.value = value;
    }

    protected ArgumentError(@NonNull Builder<T, X, ?> builder) {
        this(builder.operation, builder.parameter, builder.value, builder.exceptionSource, builder.message);
    }

    protected ArgumentError(@NonNull ArgumentError<T, ? extends X> source) {
        super(source);
        this.operation = source.operation;
        this.parameter = source.parameter;
        this.value = source.value;
        this.resolved = source.resolved;
        this.resolvedValue = source.resolvedValue;
    }

    public Operation getOperation() {
        return operation;
    }

    public Parameter<T> getParameter() {
        return parameter;
    }

    public Class<T> getExpectedType() {
        return getParameter().getExpected();
    }

    public Object getValue() {
        return value;
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
        if (getExpectedType() != null) {
            resolvedValue = getExpectedType().cast(resolvedValue);
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

    @Override
    public String toString() {
        return operation + " " +
                parameter + ", got" +
                value + ": " +
                super.toString();
    }
    
    public static class Operation {
        
        private final @NonNull Object context;
        private final @NonNull String operation;

        public Operation(@NonNull Object context, @NonNull String operation) {
            this.context = context;
            this.operation = operation;
        }

        public @NonNull Object getContext() {
            return context;
        }

        public @NonNull String getOperation() {
            return operation;
        }

        @Override
        public int hashCode() {
            return CMObjects.hashCode(7, 29, context, operation);
        }

        @Override
        public boolean equals(Object obj) {
            return CMObjects.safeEquals(this, obj, other ->
                Objects.equals(this.operation, other.operation) &&
                Objects.equals(this.context, other.context));
        }

        @Override
        public String toString() {
            return context + " #" + operation;
        }
    }
    
    public static class Parameter<T> {
        
        private final @NonNull String name;
        private final @NonNull Class<T> expected;

        public Parameter(String name, Class<T> expected) {
            this.name = name;
            this.expected = expected;
        }

        public String getName() {
            return name;
        }

        public Class<T> getExpected() {
            return expected;
        }

        @Override
        public int hashCode() {
            return CMObjects.hashCode(3, 67, name, expected);
        }

        @Override
        public boolean equals(Object obj) {
            return CMObjects.safeEquals(this, obj, other ->
                Objects.equals(this.name, other.name) &&
                Objects.equals(this.expected, other.expected));
        }
    }
    
    public static interface ActualValueStep<State> {
        
        OperationStep<State> got(@Nullable Object value);
    }
    
    public static interface OperationStep<State> {
        
        State in(@NonNull Object context, @NonNull String operation, @NonNull String parameter);
    }
    
    protected static abstract class Builder<T, X extends Exception, State extends ArgumentError<T, X>> implements ActualValueStep<State>, OperationStep<State> {
        
        private final Class<T> expected;
        protected final @NonNull Function<? super ErrorState<?>, ? extends X> exceptionSource;
        protected final @NonNull String message;
        protected Operation operation;
        protected Parameter<T> parameter;
        protected Object value;

        public Builder(Class<T> expected, Function<? super ErrorState<?>, ? extends X> exceptionSource, String message) {
            this.expected = expected;
            this.exceptionSource = exceptionSource;
            this.message = message;
        }

        @Override
        public OperationStep<State> got(Object value) {
            this.value = value;
            return this;
        }

        @Override
        @SuppressWarnings("ThrowableResultIgnored")
        public State in(Object context, String operation, String parameter) {
            this.operation = new Operation(context, operation);
            this.parameter = new Parameter<>(parameter, expected);
            return build();
        }

        protected abstract State build();
    }
    
    private static class ArgumentErrorBuilder<T, X extends Exception> extends Builder<T, X, ArgumentError<T, X>> {

        public ArgumentErrorBuilder(Class<T> expected, Function<? super ErrorState<?>, ? extends X> exceptionSource, String message) {
            super(expected, exceptionSource, message);
        }

        @Override
        protected ArgumentError<T, X> build() {
            return new ArgumentError<>(this);
        }
    }
}
