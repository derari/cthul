package org.cthul.monad.util;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.function.Supplier;

public interface RelativeDateTime {

    static TemporalParser.Builder parser() {
        return TemporalParser.builder();
    }

    static RelativeDateTime epoch() {
        return TemporalParser.EPOCH;
    }

    static RelativeDateTime min() {
        return TemporalParser.MIN;
    }

    static RelativeDateTime max() {
        return TemporalParser.MAX;
    }

    static RelativeDateTime zero() {
        return TemporalParser.ZERO;
    }

    static RelativeDateTime now() {
        return at(OffsetDateTime.now(), OffsetDateTime::now);
    }

    static RelativeDateTime at(OffsetDateTime dateTimeow) {
        return at(dateTimeow, OffsetDateTime::now);
    }

    static RelativeDateTime at(OffsetDateTime dateTime, Supplier<OffsetDateTime> now) {
        return new Frozen(dateTime, now);
    }

    static RelativeDateTime relative(boolean isPast, Duration duration) {
        return relative(isPast, duration, OffsetDateTime::now);
    }

    static RelativeDateTime relative(boolean isPast, Duration duration, Supplier<OffsetDateTime> now) {
        if (isPast) return since(duration, now);
        return until(duration, now);
    }

    static RelativeDateTime since(Duration past) {
        return since(past, OffsetDateTime::now);
    }

    static RelativeDateTime since(Duration past, Supplier<OffsetDateTime> now) {
        if (past.isNegative()) return new Future(past.negated(), now);
        return new Past(past, now);
    }

    static RelativeDateTime until(Duration future) {
        return until(future, OffsetDateTime::now);
    }

    static RelativeDateTime until(Duration future, Supplier<OffsetDateTime> now) {
        if (future.isNegative()) return new Past(future.negated(), now);
        return new Future(future, now);
    }

    OffsetDateTime asDateTime();

    Duration asPast();

    Duration asFuture();

    RelativeDateTime frozen();

    RelativeDateTime ticking();

    RelativeDateTime relativeTo(Supplier<OffsetDateTime> now);

    default int compareTo(RelativeDateTime other) {
        return asDateTime().compareTo(other.asDateTime());
    }

    default boolean isEqualOrBefore(RelativeDateTime other) {
        return compareTo(other) <= 0;
    }

    default boolean isBefore(RelativeDateTime other) {
        return compareTo(other) < 0;
    }

    default boolean isAfter(RelativeDateTime other) {
        return compareTo(other) > 0;
    }

    abstract class Relative implements RelativeDateTime {
        protected final Supplier<OffsetDateTime> now;
        public Relative(Supplier<OffsetDateTime> now) {
            this.now = now;
        }
    }

    class Frozen extends Relative {
        private final OffsetDateTime dateTime;
        public Frozen(OffsetDateTime dateTime, Supplier<OffsetDateTime> now) {
            super(now);
            this.dateTime = dateTime;
        }
        @Override
        public OffsetDateTime asDateTime() {
            return dateTime;
        }
        @Override
        public Duration asPast() {
            return Duration.between(dateTime, now.get());
        }
        @Override
        public Duration asFuture() {
            return Duration.between(now.get(), dateTime);
        }
        @Override
        public RelativeDateTime frozen() {
            return this;
        }
        @Override
        public RelativeDateTime ticking() {
            return until(asFuture(), now);
        }
        @Override
        public RelativeDateTime relativeTo(Supplier<OffsetDateTime> now) {
            return at(dateTime, now);
        }
        @Override
        public String toString() {
            return dateTime.toString();
        }
    }

    abstract class Ticking extends Relative {
        public Ticking(Supplier<OffsetDateTime> now) {
            super(now);
        }
        @Override
        public RelativeDateTime frozen() {
            return at(asDateTime(), now);
        }
        @Override
        public RelativeDateTime ticking() {
            return this;
        }
    }

    class Past extends Ticking {
        private final Duration past;
        public Past(Duration past, Supplier<OffsetDateTime> now) {
            super(now);
            this.past = past;
        }
        @Override
        public OffsetDateTime asDateTime() {
            return now.get().minus(past);
        }
        @Override
        public Duration asPast() {
            return past;
        }
        @Override
        public Duration asFuture() {
            return past.negated();
        }
        @Override
        public RelativeDateTime relativeTo(Supplier<OffsetDateTime> now) {
            return since(past, now);
        }
        @Override
        public int compareTo(RelativeDateTime other) {
            return other.asPast().compareTo(past);
        }
        @Override
        public String toString() {
            return "since " + past.toString();
        }
    }

    class Future extends Ticking {
        private final Duration future;
        public Future(Duration future, Supplier<OffsetDateTime> now) {
            super(now);
            this.future = future;
        }
        @Override
        public OffsetDateTime asDateTime() {
            return now.get().plus(future);
        }
        @Override
        public Duration asPast() {
            return future.negated();
        }
        @Override
        public Duration asFuture() {
            return future;
        }
        @Override
        public RelativeDateTime relativeTo(Supplier<OffsetDateTime> now) {
            return until(future, now);
        }
        @Override
        public int compareTo(RelativeDateTime other) {
            return future.compareTo(other.asFuture());
        }
        @Override
        public String toString() {
            return "until " + future.toString();
        }
    }
}
