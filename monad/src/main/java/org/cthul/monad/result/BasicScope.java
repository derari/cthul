package org.cthul.monad.result;

import org.cthul.monad.ScopedException;
import org.cthul.monad.ScopedRuntimeException;

public class BasicScope extends NamedScope {

    private final ScopedException.Type checked = ScopedException.withScope(this);
    private final ScopedRuntimeException.Type unchecked = ScopedRuntimeException.withScope(this);

    public BasicScope(String name) {
        super(name);
    }

    public ScopedException.Type checked() {
        return checked;
    }

    @Override
    public ScopedRuntimeException.Type unchecked() {
        return unchecked;
    }

    @Override
    public ScopedRuntimeException.Type uncheckedException() {
        return unchecked;
    }

    @Override
    public ScopedException parseMessage(ResultMessage resultMessage) {
        return checked().parseMessage(resultMessage);
    }
}
