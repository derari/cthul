package org.cthul.monad.util;

import org.cthul.monad.result.NoValueFactory;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;

public interface ExceptionType<X extends Exception> extends NoValueFactory<X> {

    X exception(Status status, String message, Throwable cause);

    default X exception(Status status, String message) {
        return exception(status, message, (Throwable) null);
    }

    default X exception(Status status, String message, Object... args) {
        if (args == null || args.length == 0) {
            return exception(status, message, (Throwable) null);
        }
        Throwable cause = null;
        Object last = args[args.length - 1];
        if (last instanceof Throwable) {
            cause = (Throwable) last;
        } else if (last instanceof Unsafe<?,?> && !((Unsafe) last).isPresent()) {
            cause = ((Unsafe) last).getException();
        }
        message = SafeStrings.format(message, args);
        return exception(status, message, cause);
    }

    default X exception(Status status, Throwable cause) {
        return exception(status, null, cause);
    }

    default X exception(Status status) {
        return exception(status, null, (Throwable) null);
    }

    @Override
    default X noResult(Status status, String message, Throwable throwable) {
        return exception(status, message, throwable);
    }
}
