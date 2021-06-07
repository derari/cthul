package org.cthul.monad.util;

import org.cthul.monad.DefaultStatus;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;

public interface ExceptionType<X extends Exception> {
    
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
    
    default X noValue(String message, Throwable cause) {
        return exception(DefaultStatus.NO_VALUE, message, cause);
    }
    
    default X noValue(String message) {
        return exception(DefaultStatus.NO_VALUE, message, (Throwable) null);
    }
    
    default X noValue(String message, Object... args) {
        return exception(DefaultStatus.NO_VALUE, message, args);
    }
    
    default X noValue(Throwable cause) {
        return exception(DefaultStatus.NO_VALUE, null, cause);
    }
    
    default X noValue() {
        return exception(DefaultStatus.NO_VALUE, null, (Throwable) null);
    }
    
    default X notFound(String message, Throwable cause) {
        return exception(DefaultStatus.NOT_FOUND, message, cause);
    }
    
    default X notFound(String message) {
        return exception(DefaultStatus.NOT_FOUND, message, (Throwable) null);
    }
    
    default X notFound(String message, Object... args) {
        return exception(DefaultStatus.NOT_FOUND, message, args);
    }
    
    default X notFound(Throwable cause) {
        return exception(DefaultStatus.NOT_FOUND, null, cause);
    }
    
    default X notFound() {
        return exception(DefaultStatus.NOT_FOUND, null, (Throwable) null);
    }
    
    default X internal(String message, Throwable cause) {
        return exception(DefaultStatus.INTERNAL_ERROR, message, cause);
    }
    
    default X internal(String message) {
        return exception(DefaultStatus.INTERNAL_ERROR, message, (Throwable) null);
    }
    
    default X internal(String message, Object... args) {
        return exception(DefaultStatus.INTERNAL_ERROR, message, args);
    }
    
    default X internal(Throwable cause) {
        return exception(DefaultStatus.INTERNAL_ERROR, null, cause);
    }
    
    default X internal() {
        return exception(DefaultStatus.INTERNAL_ERROR, null, (Throwable) null);
    }
}
