package org.cthul.monad.cache;

import java.util.stream.Stream;
import org.cthul.monad.util.RelativeDateTime;

import static org.cthul.monad.cache.CacheControl.Builder.*;

public interface CacheInfo {

    RelativeDateTime getAge();

    RelativeDateTime getMaxAge();

    boolean isCacheable();

    boolean isStorable();

    static Builder noStore() {
        return new Builder().storable(false);
    }

    static Builder cacheable() {
        return new Builder();
    }

    static Builder lastModified(RelativeDateTime age) {
        return new Builder().age(age);
    }

    class Builder implements CacheInfo {

        RelativeDateTime age;
        RelativeDateTime maxAge;
        boolean cacheable;
        boolean storable;

        public Builder() {
            age = RelativeDateTime.zero();
            maxAge = RelativeDateTime.max();
            cacheable = true;
            storable = true;
        }

        public Builder(CacheInfo source) {
            age = source.getAge();
            maxAge = source.getMaxAge();
            cacheable = source.isCacheable();
            storable = source.isStorable();
        }

        public Builder merge(CacheInfo other) {
            Builder self = this;
            if (age.compareTo(other.getAge()) < 0) {
                self = self.age(other.getAge());
            }
            if (maxAge.compareTo(other.getMaxAge()) > 0) {
                self = self.maxAge(other.getMaxAge());
            }
            self = self.cacheable(cacheable && other.isCacheable());
            self = self.storable(storable && other.isStorable());
            return self;
        }

        public Builder age(RelativeDateTime age) {
            if (age == null) throw new NullPointerException("age");
            this.age = age;
            return this;
        }

        public Builder maxAge(RelativeDateTime maxAge) {
            if (maxAge == null) throw new NullPointerException("maxAge");
            this.maxAge = maxAge;
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
                    return cacheable(!parseBoolean(part, eq, true));
                case "cache":
                    return cacheable(parseBoolean(part, eq, true));
                case "no-store":
                    return storable(!parseBoolean(part, eq, true));
                case "store":
                    return storable(parseBoolean(part, eq, true));
                case "age":
                    return age(parseAge(part, eq).frozen());
                case "max-age":
                    return maxAge(parseFresh(part, eq).frozen());
            }
            return this;
        }

        @Override
        public RelativeDateTime getAge() {
            return age;
        }

        @Override
        public RelativeDateTime getMaxAge() {
            return maxAge;
        }

        @Override
        public boolean isCacheable() {
            return cacheable && storable;
        }

        @Override
        public boolean isStorable() {
            return storable;
        }

        public Builder copy() {
            return new Builder(this);
        }

        public CacheInfo readOnly() {
            class ReadOnly implements Delegator {
                @Override
                public CacheInfo getCacheInfo() {
                    return Builder.this;
                }
                @Override
                public String toString() {
                    return Builder.this.toString();
                }
            }
            return new ReadOnly();
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            if (!isStorable()) sb.append(" no-store");
            if (!isCacheable()) sb.append(" no-cache");
            if (getMaxAge().isBefore(RelativeDateTime.max())) sb.append(" max-age=").append(getMaxAge().asFuture().getSeconds());
            if (getAge().isBefore(RelativeDateTime.zero())) sb.append(" age=").append(getAge().asPast().getSeconds());
            return sb.isEmpty() ? "" : sb.substring(1);
        }
    }

    interface Delegator extends CacheInfo {

        CacheInfo getCacheInfo();

        @Override
        default RelativeDateTime getAge() {
            return getCacheInfo().getAge();
        }

        @Override
        default RelativeDateTime getMaxAge() {
            return getCacheInfo().getMaxAge();
        }

        @Override
        default boolean isCacheable() {
            return getCacheInfo().isCacheable();
        }

        @Override
        default boolean isStorable() {
            return getCacheInfo().isStorable();
        }
    }
}
