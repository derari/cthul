package org.cthul.monad.result;

import java.util.function.Supplier;
import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.ValueIsPresentException;

public class RuntimeExceptionResult extends AbstractUnsafe<Object, RuntimeException> implements NoResult {
    
    private final Supplier<RuntimeException> exceptionSupplier;

    public RuntimeExceptionResult(Scope scope, Status status, Supplier<RuntimeException> exceptionSupplier) {
        super(scope, status);
        this.exceptionSupplier = exceptionSupplier;
    }

    public RuntimeExceptionResult(Scope scope, Status status, RuntimeException exception) {
        this(scope, status, () -> exception);
    }
    
    @Override
    public RuntimeException getException() throws ValueIsPresentException {
        return exceptionSupplier.get();
    }
}
