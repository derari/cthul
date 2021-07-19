package org.cthul.monad.error;

import java.util.function.Supplier;
public class IllegalArgument<T, X extends Exception> extends ArgumentError<T, X> {

    public static Builder builder() {
        return new BuilderImpl();
    }

    public IllegalArgument(Object context, String operation, String parameter, Class<T> expected, Object value, String error, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, value, error, exceptionSource);
    }

    protected IllegalArgument(ArgumentError<T, ? extends X> source) {
        super(source);
    }

    @Override
    public T getValue() {
        if (isResolved()) return peekResolvedValue();
        return (T) super.getValue();
    }

    public void setValue(T resolvedValue) {
        setResolvedValue(resolvedValue);
    }

    protected static class BuilderImpl extends ArgumentError.BuilderImpl {

        @Override
        protected ArgumentError<?, ?> build(Object context, String operation, String parameter, Class<?> expected, Object value, String error, Supplier<? extends Exception> exceptionSource) {
            return new IllegalArgument<>(context, operation, parameter, expected, value, error, exceptionSource);
        }
    }
}
