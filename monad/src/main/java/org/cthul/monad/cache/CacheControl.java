package org.cthul.monad.cache;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Stream;
import org.cthul.monad.util.RelativeDateTime;
import org.cthul.monad.util.TemporalParser;

public interface CacheControl {

    RelativeDateTime getMaxAge();

    RelativeDateTime getMaxStale();

    RelativeDateTime getMinFresh();

    boolean isCacheable();

    boolean isStorable();

    boolean isCacheOnly();

    class Builder implements CacheControl {

        private static final TemporalParser AGE_PARSER = TemporalParser.builder().numberIsDuration(ChronoUnit.SECONDS).durationIsPast().create();
        private static final TemporalParser FRESH_PARSER = TemporalParser.builder().numberIsDuration(ChronoUnit.SECONDS).durationIsFuture().create();

        RelativeDateTime maxAge;
        RelativeDateTime maxStale;
        RelativeDateTime minFresh;
        boolean cacheable;
        boolean storable;
        boolean cacheOnly;

        public Builder() {
            maxAge = RelativeDateTime.epoch();
            maxStale = RelativeDateTime.epoch();
            minFresh = RelativeDateTime.max();
            cacheable = true;
            storable = true;
            cacheOnly = false;
        }

        public Builder(CacheControl source) {
            maxAge = source.getMaxAge();
            maxStale = source.getMaxStale();
            minFresh = source.getMinFresh();
            cacheable = source.isCacheable();
            storable = source.isStorable();
            cacheOnly = source.isCacheOnly();
        }

        public Builder maxAge(RelativeDateTime maxAge) {
            if (maxAge == null) throw new NullPointerException("maxAge");
            this.maxAge = maxAge;
            return this;
        }

        public Builder maxStale(RelativeDateTime maxStale) {
            if (maxStale == null) throw new NullPointerException("maxStale");
            this.maxStale = maxStale;
            return this;
        }

        public Builder minFresh(RelativeDateTime minFresh) {
            if (minFresh == null) throw new NullPointerException("minFresh");
            this.minFresh = minFresh;
            return this;
        }

        public Builder cacheable(boolean cacheable) {
            this.cacheable = cacheable;
            return this;
        }

        public Builder storable(boolean storable) {
            this.storable = storable;
            return this;
        }

        public Builder cacheOnly(boolean cacheOnly) {
            this.cacheOnly = cacheOnly;
            return this;
        }

        public Builder parse(String string) {
            String[] parts = string.split(",");
            Stream.of(parts).forEach(this::parsePart);
            return this;
        }

        protected Builder parsePart(String part) {
            int eq = part.indexOf("=");
            String key = eq < 0 ? part : part.substring(0, eq);
            switch (key.trim().toLowerCase()) {
                case "no-cache":
                    return cacheable(parseBoolean(part, eq, false));
                case "cache":
                    return cacheable(parseBoolean(part, eq, true));
                case "no-store":
                    return storable(parseBoolean(part, eq, false));
                case "store":
                    return storable(parseBoolean(part, eq, true));
                case "only-if-cached":
                    return cacheOnly(parseBoolean(part, eq, true));
                case "max-age":
                    return maxAge(parseAge(part, eq));
                case "max-stale":
                    return maxStale(parseAge(part, eq));
                case "min-fresh":
                    return minFresh(parseFresh(part, eq));
            }
            return this;
        }

        static RelativeDateTime parseAge(String part, int eq) {
            if (eq < 0) {
                return RelativeDateTime.zero();
            }
            part = part.substring(eq + 1).trim().toUpperCase();
            return AGE_PARSER.parse(part, OffsetDateTime::now, RelativeDateTime::zero);
        }

        static RelativeDateTime parseFresh(String part, int eq) {
            if (eq < 0) {
                return RelativeDateTime.zero();
            }
            part = part.substring(eq + 1).trim().toUpperCase();
            return FRESH_PARSER.parse(part, OffsetDateTime::now, RelativeDateTime::zero);
        }


        static boolean parseBoolean(String part, int eq, boolean defaultValue) {
            if (eq < 0) {
                return defaultValue;
            }
            part = part.substring(eq + 1).trim().toLowerCase();
            if (TRUE_STRINGS.contains(part)) {
                return true;
            }
            if (FALSE_STRINGS.contains(part)) {
                return false;
            }
            return defaultValue;
        }

        private static final Set<String> TRUE_STRINGS = new HashSet<>(Arrays.asList("1", "true", "t", "yes", "y"));
        private static final Set<String> FALSE_STRINGS = new HashSet<>(Arrays.asList("0", "-1", "false", "f", "no", "n"));

        @Override
        public RelativeDateTime getMaxAge() {
            return maxAge;
        }

        @Override
        public RelativeDateTime getMaxStale() {
            return maxStale;
        }

        @Override
        public RelativeDateTime getMinFresh() {
            return minFresh;
        }

        @Override
        public boolean isCacheable() {
            return cacheable;
        }

        @Override
        public boolean isStorable() {
            return storable;
        }

        @Override
        public boolean isCacheOnly() {
            return cacheOnly;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (!isStorable()) sb.append(" no-store");
            if (!isCacheable()) sb.append(" no-cache");
            if (isCacheOnly()) sb.append(" only-if-cached");
            if (getMaxAge().isAfter(RelativeDateTime.epoch())) sb.append(" max-age=").append(getMaxAge().asPast().getSeconds());
            if (getMaxStale().isAfter(RelativeDateTime.epoch())) sb.append(" max-stale=").append(getMaxStale().asPast().getSeconds());
            if (getMinFresh().isBefore(RelativeDateTime.max())) sb.append(" min-fresh=").append(getMinFresh().asFuture().getSeconds());
            return sb.isEmpty() ? "" : sb.substring(1);
        }
    }

    interface Delegator extends CacheControl {

        CacheControl getCacheControl();

        @Override
        default RelativeDateTime getMaxAge() {
            return getCacheControl().getMaxAge();
        }

        @Override
        default RelativeDateTime getMaxStale() {
            return getCacheControl().getMaxStale();
        }

        @Override
        default RelativeDateTime getMinFresh() {
            return getCacheControl().getMinFresh();
        }

        @Override
        default boolean isCacheable() {
            return getCacheControl().isCacheable();
        }

        @Override
        default boolean isStorable() {
            return getCacheControl().isStorable();
        }

        @Override
        default boolean isCacheOnly() {
            return getCacheControl().isCacheOnly();
        }
    }
}
