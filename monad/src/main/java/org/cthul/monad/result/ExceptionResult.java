package org.cthul.monad.result;

import java.util.function.Supplier;
import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.ValueIsPresentException;

public class ExceptionResult<X extends Exception> extends AbstractUnsafe<Object, X> implements NoValue<X> {

    private Supplier<? extends X> exceptionSupplier;

    public ExceptionResult(Scope scope, Status status, Supplier<? extends X> exceptionSupplier) {
        super(scope, status);
        this.exceptionSupplier = exceptionSupplier;
    }

    public ExceptionResult(Scope scope, Status status, X exception) {
        this(scope, status, () -> exception);
    }

    @Override
    public X getException() throws ValueIsPresentException {
        return exceptionSupplier.get();
    }
}
