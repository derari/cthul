package org.cthul.monad.util;

import org.cthul.monad.Scope;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.adapt.ResultWrapper;
import org.cthul.monad.adapt.UnsafeAdapter;
import org.cthul.monad.result.NoValue;
import org.cthul.monad.result.ValueResult;
import org.cthul.monad.switches.BasicSwitch;
import org.cthul.monad.switches.Switch;

public class ScopedResultWrapper implements ResultWrapper {

    final Scope scope;
    final boolean forceScope;
    final ResultAdapter resultAdapter;

    public ScopedResultWrapper(Scope scope, boolean forceScope) {
        this.scope = scope;
        this.forceScope = forceScope;
        this.resultAdapter = new ResultAdapter(scope::internal);
    }

    public ScopedResultWrapper(Scope scope, boolean forceScope, ResultAdapter resultAdapter) {
        this.scope = scope;
        this.forceScope = forceScope;
        this.resultAdapter = resultAdapter;
    }

    protected ResultWrapper extend(ResultAdapter resultAdapter) {
        return new ScopedResultWrapper(scope, forceScope, resultAdapter);
    }

    @Override
    public BasicSwitch<Unsafe<?, ?>, Unsafe<?, ?>, UnsafeAdapter, ? extends ResultWrapper> adaptResult() {
        Switch choice = resultAdapter.chooseResult()
                .<UnsafeAdapter>mapTarget((unsafe, adapter) -> adapter.unsafe(unsafe))
                .mapResult(this::extend);
        return choice.asBasicSwitch();
    }

    @Override
    public <X extends Exception> NoValue<X> failed(X exception) {
        return (NoValue) forceScope(resultAdapter.adaptException(exception));
    }

    @Override
    public <T> ValueResult<T> value(Status status, T value) {
        return scope.value(status, value);
    }

    @Override
    public <T, X extends Exception> Unsafe<T, X> unsafe(Unsafe<T, X> unsafe) {
        return (Unsafe) forceScope(resultAdapter.adaptUnsafe(unsafe));
    }

    private <T, X extends Exception> Unsafe<T, X> forceScope(Unsafe<T, X> unsafe) {
        if (!forceScope || scope.sameScope(unsafe.getScope())) return unsafe;
        return unsafe
                .flatMap(t -> scope.value(unsafe.getStatus(), t).unsafe())
                .flatMapException(x -> scope.failed(unsafe.getStatus(), x).unsafe());
    }
}
