package org.cthul.monad.util;

import java.util.Arrays;
import java.util.Random;
import org.cthul.monad.Status;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public abstract class GenericScopeBase<X extends Exception> implements GenericScope<X> {
    
    protected final String name;
    protected final Logger log;

    public GenericScopeBase(String name) {
        this.name = name;
        this.log = LoggerFactory.getLogger(name);
    }

    public GenericScopeBase(String name, Logger log) {
        this.name = name;
        this.log = log;
    }
    
    @Override
    public X exception(Status status, String message, Object... args) {
        Object last = (args != null && args.length > 0) ? args[0] : null;
        message = format(message, args);
        if (last instanceof Throwable) {
            return exception(status, message, (Throwable) last);
        } else {
            return exception(status, message);
        }
    }

    @Override
    public X internalLoggedException(Status status, String msg, Object... args) {
        String id = Long.toHexString(new Random().nextLong());
        logError(id, msg, args);
        return exception(status, "%s (%s)", status.getDescription(), id);
    }
    
    protected void logError(String id, String msg, Object... args) {
        if (!log.isErrorEnabled()) {
            return;
        }
        Marker m = getErrorMarker(id);
        if (msg.contains("{}")) {
            Object[] allArgs = Arrays.copyOf(args, args.length + 1);
            allArgs[args.length] = allArgs[args.length - 1];
            allArgs[args.length - 1] = id;
            log.error(m, msg, allArgs);
            return;
        } 
        Object last = args[args.length - 1];
        args[args.length - 1] = id;
        if (msg.contains("%s")) {
            msg = format(msg, args);
        } else {
            msg = SafeStrings.safeConcatenate(msg, args);
        }
        log.error(m, msg, last);
    }
    
    protected Marker getErrorMarker(String errorId) {
        return MarkerFactory.getDetachedMarker(errorId);
    }
    
    @Override
    public X parseMessage(ResultMessage message) {
        Status status = Status.withDescription(message.getCode(), message.getStatus());
        String scope = message.getScope();
        String text = message.getMessage();
        if (scope != null && !scope.isEmpty() && !scope.equals(name)) {
            text = scope + ": " + text;
        }
        return exception(status, text);
    }
    
    protected String format(String format, Object[] args) {
        return SafeStrings.format(format, args);
    }

    @Override
    public String toString() {
        return name;
    }
}
