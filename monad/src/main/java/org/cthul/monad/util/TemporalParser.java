package org.cthul.monad.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class TemporalParser {

    public static Builder builder() {
        return new Builder();
    }

    private final TemporalUnit durationUnit;
    private final boolean durationPast;

    public TemporalParser(TemporalUnit durationUnit, boolean durationPast) {
        this.durationUnit = durationUnit;
        this.durationPast = durationPast;
    }

    public RelativeDateTime parse(String input) {
        return parse(input, OffsetDateTime::now, () -> null);
    }

    public RelativeDateTime parse(String input, Supplier<OffsetDateTime> now, Supplier<RelativeDateTime> defaultValue) {
        if (input == null) return defaultValue.get();
        input = input.strip().toUpperCase();
        if (input.isEmpty()) return defaultValue.get();
        if (isInteger(input)) {
            long n = Long.parseLong(input);
            if (durationUnit != null) {
                return RelativeDateTime.relative(durationPast, Duration.of(n, durationUnit), now);
            }
            return RelativeDateTime.at(DT_EPOCH.plus(n, ChronoUnit.SECONDS), now);
        }
        if (input.contains(":")) {
            if (input.contains(",")) {
                return RelativeDateTime.at(OffsetDateTime.parse(input, DateTimeFormatter.RFC_1123_DATE_TIME), now);
            }
            return RelativeDateTime.at(OffsetDateTime.parse(input), now);
        }
        input = normalizeDurationString(input);
        if (input.contains("T")) {
            return RelativeDateTime.relative(durationPast, Duration.parse(input), now);
        }
        Period p = Period.parse(input);
        OffsetDateTime nowDt = now.get();
        return RelativeDateTime.relative(durationPast, Duration.between(nowDt, nowDt.plus(p)), now);
    }

    private String normalizeDurationString(String input) {
        if (!input.startsWith("P") && !input.startsWith("-P")) {
            input = "P" + input;
        }
        if (!input.contains("T")) {
            int d = input.indexOf('D');
            if (d < 0) d = input.indexOf('P');
            if (d + 1 < input.length()) {
                input = input.substring(0, d + 1) + "T" + input.substring(d + 1);
            }
        }
        if (input.endsWith("T")) {
            input += "0S";
        }
        if (input.endsWith("P")) {
            input += "0D";
        }
        if (input.charAt(input.length() - 1) <= '9') {
            input = input + "S";
        }
        return input;
    }

    private boolean isInteger(String input) {
        return P_INT.matcher(input).matches();
    }

    private static final Pattern P_INT = Pattern.compile("[-+]?\\d+");
    private static final OffsetDateTime DT_EPOCH = OffsetDateTime.of(1970, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
    static final RelativeDateTime EPOCH = RelativeDateTime.at(DT_EPOCH, OffsetDateTime::now);
    static final RelativeDateTime MIN = RelativeDateTime.at(OffsetDateTime.MIN, OffsetDateTime::now);
    static final RelativeDateTime MAX = RelativeDateTime.at(OffsetDateTime.MAX, OffsetDateTime::now);
    static final RelativeDateTime ZERO = RelativeDateTime.until(Duration.ZERO, OffsetDateTime::now);

    public static class Builder {

        private TemporalUnit durationUnit = null;
        private boolean durationPast = false;

        public Builder numberIsDuration(TimeUnit unit) {
            return numberIsDuration(unit.toChronoUnit());
        }

        public Builder numberIsDuration(TemporalUnit unit) {
            durationUnit = unit;
            return this;
        }

        public Builder numberIsTimestamp() {
            durationUnit = null;
            return this;
        }

        public Builder durationIsPast() {
            durationPast = true;
            return this;
        }

        public Builder durationIsFuture() {
            durationPast = false;
            return this;
        }

        public TemporalParser create() {
            return new TemporalParser(durationUnit, durationPast);
        }
    }
}
