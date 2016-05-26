package com.github.herrschwarz.status4spring.cache;

import com.google.common.cache.Cache;
import org.springframework.cache.guava.GuavaCacheManager;

import java.util.List;
import java.util.stream.Collectors;


public class GuavaCacheStatsProvider implements CacheStatsProvider {

    private GuavaCacheManager cacheManager;

    public GuavaCacheStatsProvider(GuavaCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }


    // @TODO: trait?
    @Override
    public List<CacheStats> getCacheStats() {
        return cacheManager.getCacheNames()
                .stream()
                .map(this::computeStats)
                .collect(Collectors.toList());
    }

    @Override
    public Long clearCache(String name) {
        Cache nativeCache = (Cache) cacheManager.getCache(name).getNativeCache();
        Long count = nativeCache.size();
        nativeCache.invalidateAll();
        return count;
    }

    private CacheStats computeStats(String name) {
        CacheStats cacheStats = new CacheStats();
        Cache cache = (Cache) cacheManager.getCache(name).getNativeCache();
        com.google.common.cache.CacheStats stats = cache.stats();
        cacheStats.setName(name);
        cacheStats.setType("Guava Cache");
        cacheStats.setNumberOfEntries(cache.size());
        cacheStats.setNumberOfHits(stats.hitCount());
        return cacheStats;
    }
}
