package org.cthul.monad.util;

import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.function.ResultWrapper;
import org.cthul.monad.result.NoResult;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ValueResult;
import org.cthul.monad.switches.BasicSwitch;
import org.cthul.monad.switches.SwitchFunctionBuilder;

public class ScopedResultWrapper extends UnsafeStatusAdapter implements ResultWrapper {

    private final Scope scope;

    public ScopedResultWrapper(Scope scope) {
        super(scope::internal);
        this.scope = scope;
    }

    protected ScopedResultWrapper(ScopedResultWrapper parent, CheckedFunction<Unsafe<?, ?>, Status, ?> statusMapper) {
        super(parent, statusMapper);
        this.scope = parent.scope;
    }
    
    public ResultWrapper withStatusMapper(CheckedFunction<Unsafe<?, ?>, Status, ?> statusMapper) {
        return new ScopedResultWrapper(this, statusMapper);
    }

    @Override
    public <X extends Exception> NoValue<X> failed(X exception) {
        return (NoValue) apply(exception);
    }

    @Override
    public <T> ValueResult<T> value(T value) {
        return scope.value(value);
    }

    @Override
    public NoResult okNoValue() {
        return scope.okNoValue();
    }

    @Override
    public BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, Status, ? extends ResultWrapper> withStatus() {
        return SwitchFunctionBuilder.functionBuilder(this::withStatusMapper).asBasicSwitch();
    }
}
