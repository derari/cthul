package org.cthul.monad.util;

import java.util.function.Function;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.ExceptionWrapper;
import org.cthul.monad.function.RuntimeExceptionWrapper;
import org.cthul.monad.result.NoResult;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ValueResult;
import org.cthul.monad.switches.BasicSwitch;
import org.cthul.monad.switches.SwitchFunctionBuilder;

public class UnsafeStatusAdapter {
    
    private final Function<Exception, NoValue<?>> parentWrapper;
    private final CheckedFunction<Unsafe<?, ?>, Status, ?> statusMapper;

    public UnsafeStatusAdapter(Function<Exception, NoValue<?>> parentWrapper) {
        this.parentWrapper = parentWrapper;
        this.statusMapper = Unsafe::getStatus;
    }

    public UnsafeStatusAdapter(UnsafeStatusAdapter parent, CheckedFunction<Unsafe<?, ?>, Status, ?> statusMapper) {
        this.parentWrapper = parent::apply;
        this.statusMapper = statusMapper;
    }
    
    private NoValue<?> parentWrap(Exception exception) {
        return parentWrapper.apply(exception);
    }
    
    protected NoValue<?> apply(Exception exception) {
        return apply(parentWrap(exception));
    }

    @SuppressWarnings("UseSpecificCatch")
    protected <T, X extends Exception> Unsafe<T, X> apply(Unsafe<T, X> unsafe) {
        if (unsafe.isPresent()) return unsafe;
        return apply((NoValue<X>) unsafe).asUnsafe();
    }
    
    @SuppressWarnings("UseSpecificCatch")
    protected <X extends Exception> NoValue<X> apply(NoValue<X> unsafe) {
        Status newStatus;
        try {
            newStatus = statusMapper.apply(unsafe);
        } catch (Exception ex) {
            return unsafe.getScope().internal((X) ex);
        }
        if (unsafe.getStatus() == newStatus) return unsafe;
        return unsafe.getScope().noValue(newStatus, unsafe.getException());
    }
    
    public static class Checked<X extends Exception> extends UnsafeStatusAdapter implements ExceptionWrapper<X> {

        private final ExceptionWrapper<X> wrapper;

        public Checked(ExceptionWrapper<X> wrapper) {
            super(wrapper::failed);
            this.wrapper = wrapper;
        }

        protected Checked(Checked parent, CheckedFunction<Unsafe<?, ?>, Status, ?> statusMapper) {
            super(parent, statusMapper);
            this.wrapper = parent.wrapper;
        }

        public Checked<X> withStatusMapper(CheckedFunction<Unsafe<?, ?>, Status, ?> statusMapper) {
            return new Checked<>(this, statusMapper);
        }

        @Override
        public NoValue<X> failed(Exception exception) {
            return (NoValue) apply(exception);
        }

        @Override
        public <T> ValueResult<T> value(T value) {
            return wrapper.value(value);
        }

        @Override
        public NoResult okNoValue() {
            return wrapper.okNoValue();
        }

        @Override
        public BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, Status, ? extends ExceptionWrapper<X>> withStatus() {
            return SwitchFunctionBuilder.functionBuilder(this::withStatusMapper).asBasicSwitch();
        }
    }
    
    public static class Unchecked<X extends RuntimeException> extends UnsafeStatusAdapter implements RuntimeExceptionWrapper<X> {

        private final RuntimeExceptionWrapper<X> wrapper;

        public Unchecked(RuntimeExceptionWrapper<X> wrapper) {
            super(wrapper::failed);
            this.wrapper = wrapper;
        }

        protected Unchecked(Unchecked parent, CheckedFunction<Unsafe<?, ?>, Status, ?> statusMapper) {
            super(parent, statusMapper);
            this.wrapper = parent.wrapper;
        }

        public Unchecked<X> withStatusMapper(CheckedFunction<Unsafe<?, ?>, Status, ?> statusMapper) {
            return new Unchecked<>(this, statusMapper);
        }

        @Override
        public NoResult failed(Exception exception) {
            return apply(exception).unchecked().noValue();
        }

        @Override
        public <T> ValueResult<T> value(T value) {
            return wrapper.value(value);
        }

        @Override
        public NoResult okNoValue() {
            return wrapper.okNoValue();
        }

        @Override
        public BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, Status, ? extends RuntimeExceptionWrapper<X>> withStatus() {
            return SwitchFunctionBuilder.functionBuilder(this::withStatusMapper).asBasicSwitch();
        }
    }
}
