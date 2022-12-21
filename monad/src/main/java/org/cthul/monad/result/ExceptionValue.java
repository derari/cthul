package org.cthul.monad.result;

import org.cthul.monad.*;
import org.cthul.monad.cache.CacheInfo;
import org.cthul.monad.error.ErrorState;
import org.cthul.monad.error.GeneralError;
import org.cthul.monad.util.RelativeDateTime;

public interface ExceptionValue<X extends Exception> extends NoValue<X>, CacheInfo {

    ScopedException asScopedException();

    void setCacheControl(CacheInfo meta);

    ErrorState<?> getErrorState();

    void setErrorState(ErrorState<?> errorState);

    default String getMessage() {
        return getException().getMessage();
    }

    interface Delegator<X extends Exception> extends ExceptionValue<X>, CacheInfo.Delegator {

        ExceptionValue<?> getExceptionValue();

        @Override
        default Scope getScope() {
            return getExceptionValue().getScope();
        }

        @Override
        default Status getStatus() {
            return getExceptionValue().getStatus();
        }

        @Override
        default CacheInfo getCacheInfo() {
            return getExceptionValue();
        }

        @Override
        default ScopedException asScopedException() {
            return getExceptionValue().asScopedException();
        }

        @Override
        default void setCacheControl(CacheInfo meta) {
            getExceptionValue().setCacheControl(meta);
        }

        @Override
        default ErrorState<?> getErrorState() {
            return getExceptionValue().getErrorState();
        }

        @Override
        default void setErrorState(ErrorState<?> errorState) {
            getExceptionValue().setErrorState(errorState);
        }
    }

    class Impl<X extends Exception> implements ExceptionValue<X>, CacheInfo.Delegator {

        private final Scope scope;
        private final Status status;
        private final X exception;
        private CacheInfo cacheInfo = CacheInfo.noStore();
        private ErrorState<?> errorState;
        private ScopedException scopedException = null;

        public Impl(Scope scope, Status status, X exception) {
            if (scope == null) throw new NullPointerException("scope");
            if (status == null) throw new NullPointerException("status");
            this.scope = scope;
            this.status = status;
            this.exception = exception;
            this.errorState = new GeneralError<>(exception);
        }

        @Override
        public Scope getScope() {
            return scope;
        }

        @Override
        public Status getStatus() {
            return status;
        }

        @Override
        public X getException() throws ValueIsPresentException {
            return exception;
        }

        @Override
        public CacheInfo getCacheInfo() {
            return cacheInfo;
        }

        @Override
        public ScopedException asScopedException() {
            if (scopedException != null) {
                return scopedException;
            }
            if (exception instanceof ScopedException) {
                scopedException = (ScopedException) exception;
            } else {
                scopedException = new ScopedException(this);
            }
            return scopedException;
        }

        @Override
        public void setCacheControl(CacheInfo meta) {
            this.cacheInfo = cacheInfo;
        }

        @Override
        public ErrorState<?> getErrorState() {
            return errorState;
        }

        @Override
        public void setErrorState(ErrorState<?> errorState) {
            this.errorState = errorState;
        }
    }

    static <X extends Exception> ExceptionValue<X> of(Scope scope, Status status, X exception) {
        return new Impl<>(scope, status, exception);
    }
}
