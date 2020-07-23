package org.cthul.monad.error;

import java.util.function.Supplier;
import org.cthul.monad.Status;
import org.cthul.monad.util.GenericScope;
import org.cthul.monad.util.ScopedResult;

public class GeneralError<X extends Exception> extends AbstractErrorState<GeneralError<X>, X> {

    public GeneralError(GenericScope<? extends X> scope, Status status, String message, Object... args) {
        super(scope.noValue(status, message, args));
    }

    public GeneralError(ScopedResult<?, ? extends X> result) {
        super(result);
    }

    public GeneralError(X exception) {
        super(exception);
    }

    public GeneralError(Supplier<? extends X> exceptionSource) {
        super(exceptionSource);
    }
}
