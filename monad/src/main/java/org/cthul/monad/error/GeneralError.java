package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.util.ExceptionType;

public class GeneralError<X extends Exception> extends AbstractErrorState<GeneralError<X>, X> {

    public GeneralError(Unsafe<?, ? extends X> result) {
        super(result);
    }

    public GeneralError(X exception) {
        super(exception);
    }

    public GeneralError(Supplier<? extends X> exceptionSource) {
        super(exceptionSource);
    }

    public GeneralError(ExceptionType<? extends X> exceptionType, Status status, String message, Object... args) {
        super(exceptionType, status, message, args);
    }

    protected GeneralError(AbstractErrorState<?, ? extends X> source) {
        super(source);
    }
}
