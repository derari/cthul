package org.cthul.monad.util;

import java.util.function.Function;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;
import org.cthul.monad.function.CheckedFunction;
import org.cthul.monad.switches.BasicSwitch;
import org.cthul.monad.switches.SwitchFunctionBuilder;

public class ResultAdapter {

    public static <T, X extends Exception> Unsafe<T, X> setStatus(Unsafe<T, X> unsafe, Status newStatus) {
        if (unsafe.getStatus() == newStatus) return unsafe;
        return unsafe
            .flatMap(t -> unsafe.getScope().value(newStatus, t).unsafe())
            .flatMapException(x -> unsafe.getScope().failed(newStatus, x).unsafe());
    }

    private final Function<Exception, Unsafe<?, ?>> exceptionWrapper;
    private final CheckedFunction<Unsafe<?, ?>, Unsafe<?, ?>, ?> adapter;

    public ResultAdapter(Function<Exception, Unsafe<?, ?>> parentWrapper) {
        this.exceptionWrapper = parentWrapper;
        this.adapter = u -> u;
    }

    public ResultAdapter(ResultAdapter parent, CheckedFunction<Unsafe<?, ?>, Unsafe<?, ?>, ?> adapter) {
        this.exceptionWrapper = parent.exceptionWrapper;
        this.adapter = u -> adapter.apply(parent.adaptUnsafe(u));
    }

    public Unsafe<?,?> adaptException(Exception exception) {
        return adaptUnsafe(exceptionWrapper.apply(exception));
    }

    @SuppressWarnings("UseSpecificCatch")
    public Unsafe<?,?> adaptUnsafe(Unsafe<?,?> unsafe) {
        try {
            return adapter.apply(unsafe);
        } catch (Exception ex) {
            unsafe.ifException(ex::addSuppressed);
            return unsafe.getScope().internal(ex);
        }
    }

    public BasicSwitch.Identity<Unsafe<?,?>, Unsafe<?,?>, ResultAdapter> chooseResult() {
        return BasicSwitch.asIdentitySwitch(SwitchFunctionBuilder.functionBuilder(this::extend));
    }

    protected ResultAdapter extend(CheckedFunction<Unsafe<?, ?>, Unsafe<?, ?>, ?> adapter) {
        return new ResultAdapter(this, adapter);
    }
}
