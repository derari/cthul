package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.util.ExceptionType;

public class NullValue<T, X extends Exception> extends IllegalArgument<T, X> {

    public static Builder nullValue() {
        return new BuilderImpl();
    }

    public NullValue(Object context, String operation, String parameter, Class<T> expected, String error, Supplier<? extends X> exceptionSource) {
        super(context, operation, parameter, expected, null, error, exceptionSource);
    }

    protected NullValue(ArgumentErrorState<T, ? extends X> source) {
        super(source);
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

        <X extends Exception> BuilderCompleted<T, X> exceptionType(ExceptionType<? extends X> exceptionType);
    }

    protected static class BuilderImpl extends ArgumentErrorState.BuilderImpl implements Builder, BuilderWithOperation, BuilderWithParameter<Object> {

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
        public <X extends Exception> BuilderCompleted<Object, X> exceptionType(ExceptionType<? extends X> exceptionType) {
            return this.<X>got(null, exceptionType, "Value must not be null");
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
