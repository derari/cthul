package org.cthul.monad.util;

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.function.Supplier;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TemporalParserTest {

    private static final OffsetDateTime NOW = OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime NOW_1M = OffsetDateTime.of(2000, 1, 1, 0, 1, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime NOW_N1M = OffsetDateTime.of(1999, 12, 31, 23, 59, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime NOW_1D = OffsetDateTime.of(2000, 1, 2, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final OffsetDateTime NOW_N1D = OffsetDateTime.of(1999, 12, 31, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final Duration D1M = Duration.ofMinutes(1);
    private static final Duration DN1M = Duration.ofMinutes(-1);
    private static final Supplier<OffsetDateTime> GET_NOW = () -> OffsetDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    private static final Supplier<RelativeDateTime> GET_NULL = () -> null;

    @Test
    public void testDateTimeFromDatetime() {
        OffsetDateTime value = TemporalParser.builder()
                .numberIsTimestamp()
                .create().parse("2000-01-01T00:01:00Z").asDateTime();
        assertThat(value, is(NOW_1M));
    }

    @Test
    public void testDateTimeFromTimestamp() {
        OffsetDateTime value = TemporalParser.builder()
                .numberIsTimestamp()
                .create().parse(""+ NOW.toEpochSecond()).asDateTime();
        assertThat(value, is(NOW));
    }

    @Test
    public void testDateTimeFromPastIntegerDuration() {
        OffsetDateTime value = TemporalParser.builder()
                .numberIsDuration(ChronoUnit.MILLIS)
                .durationIsPast()
                .create().parse("60000", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_N1M));
    }

    @Test
    public void testDateTimeFromFutureIntegerDuration() {
        OffsetDateTime value = TemporalParser.builder()
                .numberIsDuration(ChronoUnit.MILLIS)
                .durationIsFuture()
                .create().parse("60000", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_1M));
    }

    @Test
    public void testDateTimeFromPastNegativeIntegerDuration() {
        OffsetDateTime value = TemporalParser.builder()
                .numberIsDuration(ChronoUnit.MILLIS)
                .durationIsPast()
                .create().parse("-60000", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_1M));
    }

    @Test
    public void testDateTimeFromPastSeconds() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsPast()
                .create().parse("60s", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_N1M));
    }

    @Test
    public void testDateTimeFromFutureSeconds() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsFuture()
                .create().parse("60s", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_1M));
    }

    @Test
    public void testDateTimeFromPastNegativeSeconds() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsPast()
                .create().parse("-60s", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_1M));
    }

    @Test
    public void testDateTimeFromPastDays() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsPast()
                .create().parse("1d", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_N1D));
    }

    @Test
    public void testDateTimeFromFutureDays() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsFuture()
                .create().parse("1d", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_1D));
    }

    @Test
    public void testDateTimeFromPastNegativeDays() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsPast()
                .create().parse("-1d", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_1D));
    }

    @Test
    public void testDateTimeFromDaysPeriod() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsPast()
                .create().parse("p1d", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_N1D));
    }

    @Test
    public void testDateTimeFromYearDaysPeriod() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsFuture()
                .create().parse("p1y-365d", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_1D));
    }

    @Test
    public void testDateTimeFromMonthDaysPeriod() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsFuture()
                .create().parse("p12m-365d", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_1D));
    }

    @Test
    public void testDateTimeFromDaysPeriodT() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsPast()
                .create().parse("p1dt", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_N1D));
    }

    @Test
    public void testDateTimeFromSecondsPeriod() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsPast()
                .create().parse("p60s", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_N1M));
    }

    @Test
    public void testDateTimeFromSecondsPeriodT() {
        OffsetDateTime value = TemporalParser.builder()
                .durationIsPast()
                .create().parse("pt60s", GET_NOW, GET_NULL).asDateTime();
        assertThat(value, is(NOW_N1M));
    }

    @Test
    public void testDurationFromDatetime() {
        Duration value = TemporalParser.builder()
                .durationIsFuture()
                .create().parse("2000-01-01T00:01:00Z", GET_NOW, GET_NULL).asFuture();
        assertThat(value, is(D1M));
    }

    @Test
    public void testDurationFromPastDatetime() {
        Duration value = TemporalParser.builder()
                .durationIsFuture()
                .create().parse("1999-12-31T23:59:00Z", GET_NOW, GET_NULL).asFuture();
        assertThat(value, is(DN1M));
    }

    @Test
    public void testDurationFromTimestamp() {
        Duration value = TemporalParser.builder()
                .numberIsTimestamp()
                .durationIsFuture()
                .create().parse(""+ NOW_1M.toEpochSecond(), GET_NOW, GET_NULL).asFuture();
        assertThat(value, is(D1M));
    }

    @Test
    public void testDurationFromTimestampPast() {
        Duration value = TemporalParser.builder()
                .numberIsTimestamp()
                .create().parse(""+ NOW_1M.toEpochSecond(), GET_NOW, GET_NULL).asPast();
        assertThat(value, is(DN1M));
    }

    @Test
    public void testDurationFromIntegerDuration() {
        Duration value = TemporalParser.builder()
                .numberIsDuration(ChronoUnit.MILLIS)
                .create().parse("60000", GET_NOW, GET_NULL).asFuture();
        assertThat(value, is(D1M));
    }

    @Test
    public void testDurationFromNegativeIntegerDuration() {
        Duration value = TemporalParser.builder()
                .numberIsDuration(ChronoUnit.MILLIS)
                .create().parse("-60000", GET_NOW, GET_NULL).asFuture();
        assertThat(value, is(DN1M));
    }

    @Test
    public void testDurationFromSeconds() {
        Duration value = TemporalParser.builder()
                .create().parse("60s", GET_NOW, GET_NULL).asFuture();
        assertThat(value, is(D1M));
    }

    @Test
    public void testDurationFromNegativeSeconds() {
        Duration value = TemporalParser.builder()
                .create().parse("-60s", GET_NOW, GET_NULL).asFuture();
        assertThat(value, is(DN1M));
    }

    @Test
    public void testDurationFromSecondsPeriod() {
        Duration value = TemporalParser.builder()
                .create().parse("p60s", GET_NOW, GET_NULL).asFuture();
        assertThat(value, is(D1M));
    }

    @Test
    public void testDurationFromSecondsPeriodT() {
        Duration value = TemporalParser.builder()
                .create().parse("pt60s", GET_NOW, GET_NULL).asFuture();
        assertThat(value, is(D1M));
    }
}