package com.malaka.aat.external.caching;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class CacheCleaner {

    private final CacheManager cacheManager;
    private static final Logger logger = LoggerFactory.getLogger(CacheCleaner.class);


    @Scheduled(fixedRate = 60 * 60_000)
    public void clearCache() {
        for (String cacheName : cacheManager.getCacheNames()) {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                logger.info("Cleared the cache {}", cacheName);
            }
        }
    }

}
