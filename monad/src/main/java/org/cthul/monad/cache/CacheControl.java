package org.cthul.monad.cache;

import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface CacheControl {
    
    Duration getMaxAge();
    
    Duration getMaxStale();
    
    Duration getMinFresh();
    
    boolean isCacheable();
    
    boolean isStorable();
    
    boolean isCacheOnly();
    
    class Builder implements CacheControl {
        
        Duration maxAge;
        Duration maxStale;
        Duration minFresh;
        boolean cacheable;
        boolean storable;
        boolean cacheOnly;

        public Builder() {
            maxAge = Duration.ZERO;
            maxStale = Duration.ZERO;
            minFresh = Duration.ZERO;
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
        
        public Builder maxAge(Duration maxAge) {
            if (maxAge == null) throw new NullPointerException("maxAge");
            this.maxAge = maxAge;
            return this;
        }

        public Builder maxStale(Duration maxStale) {
            if (maxStale == null) throw new NullPointerException("maxStale");
            this.maxStale = maxStale;
            return this;
        }
        
        public Builder minFresh(Duration minFresh) {
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
                    return maxAge(parseDuration(part, eq));
                case "max-stale":
                    return maxStale(parseDuration(part, eq));
                case "min-fresh":
                    return minFresh(parseDuration(part, eq));
            }
            return this;
        }

        static Duration parseDuration(String part, int eq) {
            if (eq < 0) {
                return Duration.ZERO;
            }
            part = part.substring(eq + 1).trim().toUpperCase();
            if (P_ONLY_DIGITS.matcher(part).matches()) {
                try {
                    long seconds = Long.parseLong(part);
                    return Duration.ofSeconds(seconds);
                } catch (NumberFormatException ex) { }
            }
            StringBuilder fullDuration = new StringBuilder();
            if (!part.contains("P")) {
                fullDuration.append('P');
            }
            int d = part.indexOf("D");
            if (part.contains("T") || d + 1 == part.length()) {
                fullDuration.append(part);
            } else {
                if (d < 0) d = part.indexOf('P');
                fullDuration.append(part, 0, d+1)
                    .append('T')
                    .append(part, d + 1, part.length());
            }
            if (part.charAt(part.length() - 1) <= '9') {
                fullDuration.append('S');
            }
            try {
                return Duration.parse(fullDuration);
            } catch (DateTimeParseException ex) {
                return Duration.ZERO;
            }
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
        
        private static final Pattern P_ONLY_DIGITS = Pattern.compile("\\d+");
        private static final Set<String> TRUE_STRINGS = new HashSet<>(Arrays.asList("1", "true", "t", "yes", "y"));
        private static final Set<String> FALSE_STRINGS = new HashSet<>(Arrays.asList("0", "-1", "false", "f", "no", "n"));
        
        @Override
        public Duration getMaxAge() {
            return maxAge;
        }

        @Override
        public Duration getMaxStale() {
            return maxStale;
        }

        @Override
        public Duration getMinFresh() {
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
    }
    
    interface Delegator extends CacheControl {
        
        CacheControl getCacheControl();

        @Override
        default Duration getMaxAge() {
            return getCacheControl().getMaxAge();
        }

        @Override
        default Duration getMaxStale() {
            return getCacheControl().getMaxStale();
        }

        @Override
        default Duration getMinFresh() {
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
