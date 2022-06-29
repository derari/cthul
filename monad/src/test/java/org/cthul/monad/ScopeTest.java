package org.cthul.monad;

import org.cthul.monad.adapt.ResultWrapper;
import org.cthul.monad.result.NoResult;
import org.cthul.monad.result.TypedScope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
        Result<String> result = new CustomException(DefaultStatus.BAD_GATEWAY, "nope").result();
        CustomException ex = assertThrows(CustomException.class, result::get);
        assertThat(ex.getMessage(), is("nope"));
    }

    @Test
    public void testCustomExceptionType() {
        assertThat(CustomException.SCOPE.unchecked().getScope(), is(CustomException.SCOPE));
        Result<String> result = CustomException.SCOPE.unchecked().badRequest("nope").result();
        CustomException ex = assertThrows(CustomException.class, result::get);
        assertThat(ex.getMessage(), is("nope"));
    }

    public static class CustomException extends ScopedRuntimeException {

        public static final TypedScope<CustomException> SCOPE = new TypedScope(CustomException::new);

        private CustomException(Scope scope, Status status, String message, Throwable cause) {
            super(scope, status, message, cause);
        }

        public CustomException(Status status, String message) {
            super(SCOPE, status, message);
        }

        public CustomException(Status status, String message, Throwable cause) {
            super(SCOPE, status, message, cause);
        }

        public CustomException(Status status, Throwable cause) {
            super(SCOPE, status, cause);
        }

        public CustomException(Status status, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(SCOPE, status, message, cause, enableSuppression, writableStackTrace);
        }
    }
}
