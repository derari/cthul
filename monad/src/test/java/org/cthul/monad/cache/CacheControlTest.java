package org.cthul.monad.cache;

import java.time.Duration;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class CacheControlTest {

    @Test
    public void testParseDuration() {
        Duration duration = new CacheControl.Builder().parse("max-age=15").getMaxAge().asPast();
        assertThat(duration.getSeconds(), is(15L));
    }

    @Test
    public void testParseDurationDay() {
        Duration duration = new CacheControl.Builder().parse("max-age=2D").getMaxAge().asPast();
        assertThat(duration.getSeconds(), is(2*86400L));
    }

    @Test
    public void testParseDurationMinutes() {
        Duration duration = new CacheControl.Builder().parse("max-age=2M").getMaxAge().asPast();
        assertThat(duration.getSeconds(), is(120L));
    }

    @Test
    public void testParseDurationDayMinutes() {
        Duration duration = new CacheControl.Builder().parse("max-age=2D2M").getMaxAge().asPast();
        assertThat(duration.getSeconds(), is(2*86400L + 120L));
    }

    @Test
    public void testParseDurationDayTMinutes() {
        Duration duration = new CacheControl.Builder().parse("max-age=2DT2M").getMaxAge().asPast();
        assertThat(duration.getSeconds(), is(2*86400L + 120L));
    }

    @Test
    public void testParseDurationDaySeconds() {
        Duration duration = new CacheControl.Builder().parse("max-age=2D2").getMaxAge().asPast();
        assertThat(duration.getSeconds(), is(2*86400L + 2L));
    }

    @Test
    public void testParseDurationDoubleNegation() {
        Duration duration = new CacheControl.Builder().parse("max-age=-P-1H").getMaxAge().asPast();
        assertThat(duration.getSeconds(), is(3600L));
    }
}
