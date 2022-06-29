package org.cthul.monad.util;

import org.cthul.monad.result.NoValueFactory;
import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.result.NoResult;
import org.cthul.monad.result.RuntimeExceptionResult;

public class NoResultFactory implements NoValueFactory<NoResult> {

    private final Scope scope;
    private final ExceptionType<? extends RuntimeException> exceptionType;

    public NoResultFactory(Scope scope) {
        this(scope, RuntimeExceptionType.getInstance());
    }

    public NoResultFactory(Scope scope, ExceptionType<? extends RuntimeException> exception) {
        this.scope = scope;
        this.exceptionType = exception;
    }

    @Override
    public NoResult noResult(Status status, String message, Throwable throwable) {
        return new RuntimeExceptionResult(scope, status, () -> exceptionType.exception(status, message, throwable));
    }
}
