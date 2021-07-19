package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.util.ExceptionType;

public class NoSuchElement<T, X extends Exception> extends ArgumentError<T, X> {

    public NoSuchElement(Object context, String operation, String parameter, Class<T> expected, Object value, String error, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, value, error, exceptionSource);
    }

    protected NoSuchElement(ArgumentError<T, ? extends X> source) {
        super(source);
    }

    public static interface Builder extends ArgumentError.Builder {

        @Override
        BuilderWithOperation operation(Object context, String operation);
    }

    public static interface BuilderWithOperation extends ArgumentError.BuilderWithOperation {

        @Override
        <T> BuilderWithParameter<T> parameter(String name, Class<T> expected);
    }

    public static interface BuilderWithParameter<T> extends ArgumentError.BuilderWithParameter<T> {

        default <X extends Exception> BuilderCompleted<T, X> got(NoValue<X> result) {
            return this.<X>got(null, result);
        }

        default <X extends Exception> BuilderCompleted<T, X> got(String error, X exception) {
            return this.<X>got(null, error, exception);
        }

        default <X extends Exception> BuilderCompleted<T, X> got(ExceptionType<? extends X> exceptionType, String error, Object... args) {
            return this.<X>got(null, exceptionType, error, args);
        }

        default <X extends Exception> BuilderCompleted<T, X> got(String error, Supplier<? extends X> exceptionSource) {
            return this.<X>got(null, error, exceptionSource);
        }

        <X extends Exception> BuilderCompleted<T, X> got(ExceptionType<? extends X> exceptionType);
    }

    protected static class BuilderImpl extends ArgumentError.BuilderImpl implements Builder, BuilderWithOperation, BuilderWithParameter<Object> {

        @Override
        public BuilderWithOperation operation(Object context, String operation) {
            return (BuilderWithOperation) super.operation(context, operation);
        }

        @Override
        public <T> BuilderWithParameter<T> parameter(String name, Class<T> expected) {
            return (BuilderWithParameter<T>) super.parameter(name, expected);
        }

        @Override
        public <X extends Exception> BuilderCompleted<Object, X> got(Object value, String error, Supplier<? extends X> exceptionSource) {
            return (BuilderCompleted) super.got(value, error, exceptionSource);
        }

        @Override
        public <X extends Exception> BuilderCompleted<Object, X> got(ExceptionType<? extends X> exceptionType) {
            return this.<X>got(null, exceptionType, "No such element");
        }

        @Override
        public ConversionFailed<Object, Object, Exception> build() {
            return (ConversionFailed) super.build();
        }

        @Override
        protected ArgumentError<?, ?> build(Object context, String operation, String parameter, Class<?> expected, Object value, String error, Supplier<? extends Exception> exceptionSource) {
            return new NoSuchElement<>(context, operation, parameter, expected, value, error, exceptionSource);
        }
    }
}
