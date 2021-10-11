package org.cthul.monad.result;

import org.cthul.monad.ScopedException;
import org.cthul.monad.ScopedRuntimeException;
import org.cthul.monad.Status;

public class BasicScope extends CustomScope {

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
    public NoResult noResult(Status status, String message, Throwable cause) {
        return unchecked().noResult(status, message, cause);
    }

    @Override
    public ScopedException parseMessage(ResultMessage resultMessage) {
        return checked().parseMessage(resultMessage);
    }
}
