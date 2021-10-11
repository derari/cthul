package org.cthul.monad;

import org.cthul.monad.adapt.ResultWrapper;
import org.cthul.monad.result.NoResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

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
}
