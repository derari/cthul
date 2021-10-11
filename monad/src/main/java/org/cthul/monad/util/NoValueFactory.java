package org.cthul.monad.util;

import org.cthul.monad.DefaultStatus;
import org.cthul.monad.Status;
import org.cthul.monad.Unsafe;

public interface NoValueFactory<T> {

    T noResult(Status status, String message, Throwable throwable);

    default T noResult(Status status, String message) {
        return noResult(status, message, (Throwable) null);
    }

    default T noResult(Status status, String message, Object... args) {
        message = SafeStrings.format(message, args);
        Throwable cause = null;
        if (args != null && args.length > 0 && args[args.length - 1] instanceof Throwable) {
            cause = (Throwable) args[args.length - 1];
        }
        return noResult(status, message, cause);
    }

    default T noValue(String message, Throwable cause) {
        return noResult(DefaultStatus.NO_VALUE, message, cause);
    }

    default T noValue(String message) {
        return noResult(DefaultStatus.NO_VALUE, message, (Throwable) null);
    }

    default T noValue(String message, Object... args) {
        return noResult(DefaultStatus.NO_VALUE, message, args);
    }

    default T noValue(Throwable cause) {
        return noResult(DefaultStatus.NO_VALUE, null, cause);
    }

    default T noValue(Status status, Throwable cause) {
        return noResult(status, null, cause);
    }

    default T noValue() {
        return noResult(DefaultStatus.NO_VALUE, null, (Throwable) null);
    }

    default T accepted() {
        return noResult(DefaultStatus.ACCEPTED, null, (Throwable) null);
    }

    default T accepted(String message, Object... args) {
        return noResult(DefaultStatus.ACCEPTED, message, args);
    }

    default T accepted(String message, Throwable cause) {
        return noResult(DefaultStatus.ACCEPTED, message, cause);
    }

    default T accepted(String message) {
        return noResult(DefaultStatus.ACCEPTED, message, (Throwable) null);
    }

    default T accepted(Throwable cause) {
        return noResult(DefaultStatus.ACCEPTED, null, cause);
    }

    default T badRequest(String message, Throwable cause) {
        return noResult(DefaultStatus.BAD_REQUEST, message, cause);
    }

    default T badRequest(String message) {
        return noResult(DefaultStatus.BAD_REQUEST, message, (Throwable) null);
    }

    default T badRequest(String message, Object... args) {
        return noResult(DefaultStatus.BAD_REQUEST, message, args);
    }

    default T badRequest(Throwable cause) {
        return noResult(DefaultStatus.BAD_REQUEST, null, cause);
    }

    default T badRequest() {
        return noResult(DefaultStatus.BAD_REQUEST, null, (Throwable) null);
    }

    default T forbidden(String message, Throwable cause) {
        return noResult(DefaultStatus.FORBIDDEN, message, cause);
    }

    default T forbidden(String message) {
        return noResult(DefaultStatus.FORBIDDEN, message, (Throwable) null);
    }

    default T forbidden(String message, Object... args) {
        return noResult(DefaultStatus.FORBIDDEN, message, args);
    }

    default T forbidden(Throwable cause) {
        return noResult(DefaultStatus.FORBIDDEN, null, cause);
    }

    default T forbidden() {
        return noResult(DefaultStatus.FORBIDDEN, null, (Throwable) null);
    }

    default T notFound(String message, Throwable cause) {
        return noResult(DefaultStatus.NOT_FOUND, message, cause);
    }

    default T notFound(String message) {
        return noResult(DefaultStatus.NOT_FOUND, message, (Throwable) null);
    }

    default T notFound(String message, Object... args) {
        return noResult(DefaultStatus.NOT_FOUND, message, args);
    }

    default T notFound(Throwable cause) {
        return noResult(DefaultStatus.NOT_FOUND, null, cause);
    }

    default T notFound() {
        return noResult(DefaultStatus.NOT_FOUND, null, (Throwable) null);
    }

    default T unprocessable(String message, Throwable cause) {
        return noResult(DefaultStatus.UNPROCESSABLE, message, cause);
    }

    default T unprocessable(String message) {
        return noResult(DefaultStatus.UNPROCESSABLE, message, (Throwable) null);
    }

    default T unprocessable(String message, Object... args) {
        return noResult(DefaultStatus.UNPROCESSABLE, message, args);
    }

    default T unprocessable(Throwable cause) {
        return noResult(DefaultStatus.UNPROCESSABLE, null, cause);
    }

    default T unprocessable() {
        return noResult(DefaultStatus.UNPROCESSABLE, null, (Throwable) null);
    }

    default T internal(String message, Throwable cause) {
        return noResult(DefaultStatus.INTERNAL_ERROR, message, cause);
    }

    default T internal(String message) {
        return noResult(DefaultStatus.INTERNAL_ERROR, message, (Throwable) null);
    }

    default T internal(String message, Object... args) {
        return noResult(DefaultStatus.INTERNAL_ERROR, message, args);
    }

    default T internal(Throwable cause) {
        return noResult(DefaultStatus.INTERNAL_ERROR, null, cause);
    }

    default T internal() {
        return noResult(DefaultStatus.INTERNAL_ERROR, null, (Throwable) null);
    }

    default T internalize(Unsafe<?, ?> unsafe) {
        return internalize(DefaultStatus.INTERNAL_ERROR, null, unsafe);
    }

    default T internalize(Status status, Object message, Unsafe<?, ?> unsafe) {
        String internalId = InternalId.getInternalId(unsafe);
        if (message == null) message = unsafe.getStatus().getDescription();
        unsafe.logInfo("{} ({}): {} -- {}", status, internalId, message);
        return noResult(status, "%s (%s)", status, internalId);
    }
}
