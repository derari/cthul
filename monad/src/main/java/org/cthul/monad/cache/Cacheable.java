package org.cthul.monad.cache;

import java.util.concurrent.CompletableFuture;

public interface Cacheable<T> {
    
    CompletableFuture<Cached<T>> async(CacheControl cacheControl);
    
    default Cached<T> get(CacheControl cacheControl) {
        return async(cacheControl).join();
    }
}