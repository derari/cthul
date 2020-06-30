package org.cthul.monad.cache;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.stream.Stream;

import static org.cthul.monad.cache.CacheControl.Builder.parseBoolean;
import static org.cthul.monad.cache.CacheControl.Builder.parseDuration;

public interface CachedMeta {
    
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
    
    class Builder implements CachedMeta {
        
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

        public Builder(CachedMeta source) {
            age = source.getAge();
            maxAge = source.getMaxAge();
            cacheable = source.isCacheable();
            storable = source.isStorable();
        }
        
        public Builder merge(CachedMeta other) {
            Builder self = this;
            if (age.compareTo(other.getAge()) < 0) {
                self = self.age(other.getAge());
            }
            if (maxAge.compareTo(other.getMaxAge()) > 0) {
                self = self.maxAge(other.getMaxAge());
            }
            self = self.cacheable(cacheable && other.isCacheable());
            self = self.cacheable(storable && other.isStorable());
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
        
        public CachedMeta readOnly() {
            class ReadOnly implements Delegator {
                @Override
                public CachedMeta getCachedMeta() {
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
    
    interface Delegator extends CachedMeta {
        
        CachedMeta getCachedMeta();

        @Override
        default Duration getAge() {
            return getCachedMeta().getAge();
        }

        @Override
        default Duration getMaxAge() {
            return getCachedMeta().getMaxAge();
        }

        @Override
        default boolean isCacheable() {
            return getCachedMeta().isCacheable();
        }

        @Override
        default boolean isStorable() {
            return getCachedMeta().isStorable();
        }
    }
}
