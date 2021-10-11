package org.cthul.monad.result;

import org.cthul.monad.DefaultStatus;
import org.cthul.monad.ScopedException;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class BasicScopeTest {

    private final BasicScope testScope = new BasicScope("TestScope");

    @Test
    public void testException() {
        IllegalStateException ex = new IllegalStateException("my exception");
        ScopedException scoped = testScope.checked().exception(DefaultStatus.BAD_REQUEST, "message: %s %s", "1", ex);

        assertThat(scoped.getCause(), is(ex));
        assertThat(scoped.getStatus(), is(DefaultStatus.BAD_REQUEST));
        assertThat(scoped.getMessage(), is("message: 1 java.lang.IllegalStateException: my exception"));

        scoped = testScope.checked().internalize(scoped.noValue()).getException();
        assertThat(scoped.getCause(), is(nullValue()));
        assertThat(scoped.getStatus(), is(DefaultStatus.INTERNAL_ERROR));
        assertThat(scoped.getMessage(), containsString("internal error ("));
    }
}
