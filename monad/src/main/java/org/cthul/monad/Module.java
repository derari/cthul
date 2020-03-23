package org.cthul.monad;

import java.util.Arrays;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Module {
    
    private final String name;
    private final Logger log;

    public Module(String name) {
        this.name = name;
        this.log = LoggerFactory.getLogger(name);
    }

    public Module(String name, Logger log) {
        this.name = name;
        this.log = log;
    }
    
    public <T> ResultValue<T> value(T value) {
        return new ResultValue<>(this, value);
    }
    
    public <T> ResultValue<T> value(Status status, T value) {
        return new ResultValue<>(this, status, value);
    }
    
    public ScopedException exception(Status status, String message, Object... args) {
        return exception(status, String.format(message, args));
    }
    
    public ScopedException exception(Status status, String message) {
        return new ScopedException(this, status, message);
    }
    
    public ScopedException exception(Status status, String message, Throwable cause) {
        return new ScopedException(this, status, message, cause);
    }
    
    public ScopedException exception(Status status, Throwable cause) {
        return new ScopedException(this, status, cause);
    }
    
    public ScopedException notFound(String message) {
        return exception(DefaultStatus.NOT_FOUND, message);
    }
    
    public ScopedException notFound(String message, Object... args) {
        return exception(DefaultStatus.NOT_FOUND, message, args);
    }
    
    public ScopedException forbidden(String message) {
        return exception(DefaultStatus.FORBIDDEN, message);
    }
    
    public ScopedException forbidden(String message, Object... args) {
        return exception(DefaultStatus.FORBIDDEN, message, args);
    }
    
    public ScopedException internal(String message, Object... args) {
        return exception(DefaultStatus.INTERNAL, message, args);
    }
    
    public ScopedException internal(Throwable cause) {
        return exception(DefaultStatus.INTERNAL, cause);
    }
    
    public ScopedException internalize(String msg, Object... args) {
        String id = Long.toHexString(new Random().nextLong());
        Object[] allArgs = Arrays.copyOf(args, args.length + 1);
        allArgs[args.length] = allArgs[args.length - 1];
        allArgs[args.length - 1] = id;
        log.error(msg, allArgs);
        return internal("Internal error: " + id);
    }
    
    public ScopedException fromCode(int code, String message, Object... args) {
        return fromCode(code, String.format(message, args));
    }
    
    public ScopedException fromCode(int code, String message) {
        return exception(DefaultStatus.fromCode(code), message);
    }
    
    public ScopedException wrap(Throwable throwable, Status defaultStatus) {
        if (throwable instanceof NoValue) {
            NoValue<?> noValue = (NoValue) throwable;
            if (noValue.getModule() == this) {
                return noValue.getException();
            }
        }
        return exception(defaultStatus, throwable);
    }
    
    public <T> Result<T> fromOptional(Optional<T> opt, String message, Object... args) {
        if (opt.isPresent()) {
            return value(opt.get());
        }
        return notFound(message, args).noValue();
    }
    
    public <T> Result<T> fromOptional(Optional<T> opt, String message) {
        if (opt.isPresent()) {
            return value(opt.get());
        }
        return notFound(message).noValue();
    }

    @Override
    public String toString() {
        return name;
    }
}
