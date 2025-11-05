package com.malaka.aat.external.caching;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CachingMonitor {

    private final CacheManager cacheManager;
    private static final Logger logger = LoggerFactory.getLogger(CachingMonitor.class);


    public long getCacheSize(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache != null && cache.getNativeCache() instanceof ConcurrentMapCache) {
            ConcurrentMapCache concurrentMapCache = (ConcurrentMapCache) cache.getNativeCache();
            return concurrentMapCache.getNativeCache().size();
        } else {
            logger.error("Error happened creating a caching mechanism");
        }
        return 0;
    }

}
