package org.cthul.monad;

import org.cthul.monad.adapt.ExceptionWrapper;
import org.cthul.monad.adapt.ResultWrapper;
import org.cthul.monad.result.*;
import org.cthul.monad.util.AbstractScopedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScopeTest {

    private final Scope instance = () -> "test";
    private final Scope other = () -> "other";

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testOverrideScope() {
        NoResult noResult = other.unchecked().noValue("no value");
        NoResult noResult2 = instance.overrideScope().noResult(noResult);
        assertThat(noResult2.getScope(), is(instance));
    }

    @Test
    public void testAsDefaultScope() {
        NoResult noResult = other.unchecked().noValue("no value");
        NoResult noResult2 = instance.asDefaultScope().noResult(noResult);
        assertThat(noResult2.getScope(), is(other));
    }

    @Test
    public void testOverrideGatewayStatus() {
        NoResult noResult = other.unchecked().noValue("no value");
        NoResult noResult2 = instance.overrideScopeAndStatus()
                    .ifTrue(u -> u.getStatus().isNotFound())
                        .set(DefaultStatus.NOT_FOUND)
                    .orElse().set(DefaultStatus.BAD_GATEWAY)
                .noResult(noResult);
        assertThat(noResult2.getStatus(), is(DefaultStatus.BAD_GATEWAY));
        assertThat(noResult2.getScope(), is(instance));
    }

    @Test
    public void testOverrideGatewayStatus2() {
        NoResult noResult = other.unchecked().notFound();
        NoResult noResult2 = instance.overrideScopeAndStatus()
                    .ifTrue(u -> u.getStatus().isNotFound())
                        .set(DefaultStatus.NOT_FOUND)
                    .orElse().set(DefaultStatus.BAD_GATEWAY)
                .noResult(noResult);
        assertThat(noResult2.getStatus(), is(DefaultStatus.NOT_FOUND));
        assertThat(noResult2.getScope(), is(instance));
    }

    @Test
    public void testConditionalOverrideScope() {
        NoResult noResult = other.unchecked().notFound();
        NoResult noResult2 = instance.asDefaultScope().adaptNoValue()
                .ifTrue(u -> u.getStatus().isNotFound())
                    .set(instance.overrideScope())
                .orElse()
                    .set(ResultWrapper.keep())
                .noResult(noResult);

        assertThat(noResult2.getScope(), is(instance));
    }

    @Test
    public void testCustomException() {
        Result<String> result = new MyRuntimeException(DefaultStatus.BAD_GATEWAY, "nope").result();
        MyRuntimeException ex = assertThrows(MyRuntimeException.class, result::get);
        assertThat(ex.getMessage(), is("nope"));
    }

    @Test
    public void testCustomExceptionType() {
        assertThat(MyRuntimeException.SCOPE.unchecked().getScope(), is(MyRuntimeException.SCOPE));
        Result<String> result = MyRuntimeException.SCOPE.unchecked().badRequest("nope").result();
        MyRuntimeException ex = assertThrows(MyRuntimeException.class, result::get);
        assertThat(ex.getMessage(), is("nope"));
    }

    @Test
    public void testCustomResultType() throws MyResultException {
        MyResult<Integer> result = myOperation(true);
        assertThat(result.get(), is(1));
    }

    @Test
    public void testCustomCheckedExceptionType() {
        MyResult<Integer> result = myOperation(false);
        MyResultException ex = assertThrows(MyResultException.class, result::get);
        assertThat(ex.getMessage(), is("no success"));
    }

    @Test
    public void testCustomSafeWrapper() {
        Unsafe<Integer, MyResultException> u = MyResultException.internal().safe().get(this::failing);
    }

    private int failing() throws IOException {
        throw new IOException("io");
    }

    private MyResult<Integer> myOperation(boolean success) {
        if (success) {
            return MyResult.value(1);
        }
        return new MyResultException(DefaultStatus.NOT_FOUND, "no success").result();
    }

    public static class MyRuntimeException extends ScopedRuntimeException {

        public static final TypedScope<MyRuntimeException> SCOPE = new TypedScope<>(MyRuntimeException::new);

        private MyRuntimeException(Scope scope, Status status, String message, Throwable cause) {
            super(scope, status, message, cause);
        }

        public MyRuntimeException(Status status, String message) {
            super(SCOPE, status, message);
        }

        public MyRuntimeException(Status status, String message, Throwable cause) {
            super(SCOPE, status, message, cause);
        }

        public MyRuntimeException(Status status, Throwable cause) {
            super(SCOPE, status, cause);
        }

        public MyRuntimeException(Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(SCOPE, status, message, cause, enableSuppression, writableStackTrace);
        }

        public MyRuntimeException(ExceptionValue<?> exceptionValue) {
            super(exceptionValue);
        }
    }

    public interface MyResult<T> extends Unsafe<T, MyResultException> {
        static <T> MyResult<T> value(T value) {
            class Ok extends AbstractOkValue<T, MyResultException> implements MyResult<T> {
                public Ok(T value) {
                    super(MyResultException.SCOPE, value);
                }
            }
            return new Ok(value);
        }

        CheckedScope<MyResultException> SCOPE = new CheckedScope<>(MyResultException::new);
    }

    public static class MyResultException extends AbstractScopedException implements ExceptionValue.Delegator<MyResultException>, MyResult<Object> {

        static ExceptionWrapper<MyResultException> internal() {
            return SCOPE.checked().withStatus(DefaultStatus.INTERNAL_ERROR);
        }

        private MyResultException(Scope scope, Status status, String message, Throwable cause) {
            super(scope, status, message, cause);
        }

        public MyResultException(Status status, String message) {
            super(SCOPE, status, message);
        }

        public MyResultException(Status status, String message, Throwable cause) {
            super(SCOPE, status, message, cause);
        }

        public MyResultException(Status status, Throwable cause) {
            super(SCOPE, status, cause);
        }

        public MyResultException(Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(SCOPE, status, message, cause, enableSuppression, writableStackTrace);
        }

        @Override
        public MyResultException getException() {
            return this;
        }

        @SuppressWarnings("unchecked")
        public <T> MyResult<T> result() {
            return (MyResult<T>) this;
        }

        @Override
        protected ScopedRuntimeException newScopedRuntimeException() {
            return new MyRuntimeException(this);
        }
    }
}
