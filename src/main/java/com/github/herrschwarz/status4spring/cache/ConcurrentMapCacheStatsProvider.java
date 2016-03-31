package com.github.herrschwarz.status4spring.cache;

import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.stream.Collectors.toList;

public class ConcurrentMapCacheStatsProvider implements CacheStatsProvider {

    private ConcurrentMapCacheManager cacheManager;
    private static final String TYPE = "Spring Cache";

    public ConcurrentMapCacheStatsProvider(ConcurrentMapCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public List<CacheStats> getCacheStats() {
        return cacheManager.getCacheNames()
                .stream()
                .map(this::computeStats)
                .collect(toList());
    }

    private CacheStats computeStats(String name) {
        ConcurrentMapCache cache = (ConcurrentMapCache) cacheManager.getCache(name);
        CacheStats cacheStats = new CacheStats();
        cacheStats.setName(name);
        cacheStats.setType(TYPE);
        Long count = ((ConcurrentHashMap) cache.getNativeCache()).mappingCount();
        cacheStats.setNumberOfEntries(count.intValue());
        return cacheStats;
    }
}
