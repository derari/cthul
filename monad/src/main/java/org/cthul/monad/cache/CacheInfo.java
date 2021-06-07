package org.cthul.monad.cache;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.cthul.monad.cache.CacheControl.Builder.parseBoolean;
import static org.cthul.monad.cache.CacheControl.Builder.parseDuration;

public interface CacheInfo {
    
    Duration getAge();
    
    Duration getMaxAge();
 
    boolean isCacheable();
    
    boolean isStorable();
    
    static Builder noStore() {
        return new Builder().storable(false);
    }
    
    static Builder cacheable() {
        return new Builder();
    }
    
    static Builder lastModified(OffsetDateTime lastModified) {
        Duration age = Duration.between(lastModified, OffsetDateTime.now());
        return new Builder().age(age);
    }
    
    class Builder implements CacheInfo {
        
        Duration age;
        Duration maxAge;
        boolean cacheable;
        boolean storable;

        public Builder() {
            age = Duration.ZERO;
            maxAge = Duration.ZERO;
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
        
        public Builder age(Duration age) {
            if (age == null) throw new NullPointerException("age");
            this.age = age;
            return this;
        }
        
        public Builder maxAge(Duration maxAge) {
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
                    return cacheable(parseBoolean(part, eq, false));
                case "cache":
                    return cacheable(parseBoolean(part, eq, true));
                case "no-store":
                    return storable(parseBoolean(part, eq, false));
                case "store":
                    return storable(parseBoolean(part, eq, true));
                case "age":
                    return age(parseDuration(part, eq));
                case "max-age":
                    return maxAge(parseDuration(part, eq));
            }
            return this;
        }

        @Override
        public Duration getAge() {
            return age;
        }

        @Override
        public Duration getMaxAge() {
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
    }
    
    interface Delegator extends CacheInfo {
        
        CacheInfo getCacheInfo();

        @Override
        default Duration getAge() {
            return getCacheInfo().getAge();
        }

        @Override
        default Duration getMaxAge() {
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
