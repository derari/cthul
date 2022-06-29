package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.util.ExceptionType;

public class ConversionFailed<S, T, X extends Exception> extends ArgumentErrorState<T, X> {

    public static Builder conversionFailed() {
        return new BuilderImpl();
    }

    public ConversionFailed(Object context, String operation, String parameter, Class<T> expected, S value, String error, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, value, error, exceptionSource);
    }

    protected ConversionFailed(ArgumentErrorState<T, ? extends X> source) {
        super(source);
    }

    @Override
    public S getValue() {
        return (S) super.getValue();
    }

    public static interface Builder extends ArgumentErrorState.Builder {

        @Override
        BuilderWithOperation operation(Object context, String operation);
    }

    public static interface BuilderWithOperation extends ArgumentErrorState.BuilderWithOperation {

        @Override
        <T> BuilderWithParameter<T> parameter(String name, Class<T> expected);
    }

    public static interface BuilderWithParameter<T> extends ArgumentErrorState.BuilderWithParameter<T> {

        <X extends Exception> BuilderCompleted<?, T, X> got(Object value, ExceptionType<? extends X> exceptionType);

        @Override
        <X extends Exception> BuilderCompleted<?, T, X> got(Object value, String error, Supplier<? extends X> exceptionSource);
    }

    public static interface BuilderCompleted<S, T, X extends Exception> extends ArgumentErrorState.BuilderCompleted<T, X> {

        @Override
        ConversionFailed<S, T, X> build();
    }

    protected static class BuilderImpl extends ArgumentErrorState.BuilderImpl implements Builder, BuilderWithOperation, BuilderWithParameter<Object>, BuilderCompleted<Object, Object, Exception> {

        @Override
        public BuilderWithOperation operation(Object context, String operation) {
            return (BuilderWithOperation) super.operation(context, operation);
        }

        @Override
        public <T> BuilderWithParameter<T> parameter(String name, Class<T> expected) {
            return (BuilderWithParameter<T>) super.parameter(name, expected);
        }

        @Override
        public <X extends Exception> BuilderCompleted<Object, Object, X> got(Object value, String error, Supplier<? extends X> exceptionSource) {
            return (BuilderCompleted) super.got(value, error, exceptionSource);
        }

        @Override
        public <X extends Exception> BuilderCompleted<?, Object, X> got(Object value, ExceptionType<? extends X> exceptionType) {
            this.<X>got(value, exceptionType, "Expected %s, got %s", getExpected(), value);
            return null;
        }

        @Override
        public ConversionFailed<Object, Object, Exception> build() {
            return (ConversionFailed) super.build();
        }

        @Override
        protected ArgumentErrorState<?, ?> build(Object context, String operation, String parameter, Class<?> expected, Object value, String error, Supplier<? extends Exception> exceptionSource) {
            return new ConversionFailed<>(context, operation, parameter, expected, value, error, exceptionSource);
        }
    }
}
