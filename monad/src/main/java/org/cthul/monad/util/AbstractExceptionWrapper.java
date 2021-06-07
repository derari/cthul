package org.cthul.monad.util;

import org.cthul.monad.Unsafe;
import org.cthul.monad.result.NoValue;

public abstract class AbstractExceptionWrapper<X extends Exception> {

    protected NoValue<X> wrap(Exception exception) {
        if (exception instanceof Unsafe) {
            Unsafe<?, ?> unsafe = (Unsafe) exception;
            if (isWrapperFor(unsafe, exception)) {
                return wrapUnsafe(unsafe.noValue());
            }
        }
        return wrapException(exception);
    }
    
    protected boolean isWrapperFor(Unsafe<?, ?> unsafe, Exception exception) {
        if (!unsafe.isPresent()) return false;
        Exception wrapped = unsafe.getException();
        if (wrapped == exception) return true;
        return exception.getClass().isInstance(wrapped);
    }
    
    protected abstract NoValue<X> wrapException(Exception exception);
    
    protected abstract NoValue<X> wrapUnsafe(NoValue<?> unsafe);
    
//    protected <T, X extends Exception> Unsafe<T, X> wrapUnsafe(Unsafe<T, X> unsafe, Status newStatus) {
//        if (unsafe.getStatus() == newStatus) return unsafe;
//        return new ExceptionResult<>(unsafe.getScope(), newStatus, unsafe.getException()).failed();
//    }
//
//    @Override
//    public BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, Status, ? extends ZResultWrapper> withStatus() {
//        return BasicSwitch.asBasicSwitch(SwitchFunctionBuilder.functionBuilder(f -> new StatusWrapper<>(this, getRootWrapper(), f)));
//    }
//
//    protected AbstractExceptionWrapper getRootWrapper() {
//        return this;
//    }
//    
//    protected static class StatusWrapper<X extends Exception> extends AbstractExceptionWrapper<X> {
//
//        private final AbstractExceptionWrapper parent;
//        private final AbstractExceptionWrapper root;
//        private final CheckedFunction<Unsafe<?, ?>, Status, ?> statusMapper;
//
//        public StatusWrapper(AbstractExceptionWrapper parent, AbstractExceptionWrapper root, CheckedFunction<Unsafe<?, ?>, Status, ?> statusMapper) {
//            this.parent = parent;
//            this.root = root;
//            this.statusMapper = statusMapper;
//        }
//
//        @Override
//        protected AbstractExceptionWrapper getRootWrapper() {
//            return root;
//        }
//
//        @Override
//        protected <T> ValueResult<T> value(T value) {
//            return root.value(value);
//        }
//
//        @Override
//        protected <T> Unsafe<T, X> wrapException(Exception failed) {
//            Unsafe<T, X> unsafe = parent.wrapException(failed);
//            return applyStatus(unsafe);
//        }
//
//        @Override
//        protected <T, X extends Exception> Unsafe<T, X> wrapUnsafe(Unsafe<T, X> unsafe) {
//            return applyStatus(unsafe);
//        }
//
//        @Override
//        protected <T, X extends Exception> Unsafe<T, X> wrapUnsafe(Unsafe<T, X> unsafe, Status newStatus) {
//            unsafe = parent.wrapUnsafe(unsafe, newStatus);
//            return applyStatus(unsafe);
//        }
//        
//        @SuppressWarnings("UseSpecificCatch")
//        protected <T, X extends Exception> Unsafe<T, X> applyStatus(Unsafe<T, X> unsafe) {
//            try {
//                Status status = statusMapper.apply(unsafe);
//                return root.wrapUnsafe(unsafe, status);
//            } catch (Exception ex) {
//                unsafe.getException().addSuppressed(ex);
//                return unsafe;
//            }
//        }
//    }
}
