package com.github.herrschwarz.status4spring.cache;

import com.google.common.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.guava.GuavaCacheManager;

import static com.github.herrschwarz.status4spring.cache.ImmutableCacheStats.builder;

public class GuavaCacheStatsProvider implements CacheStatsProvider {

  private GuavaCacheManager cacheManager;

  public GuavaCacheStatsProvider(GuavaCacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

  @Override
  public Long clearCache(String name) {
    Cache nativeCache = (Cache) cacheManager.getCache(name).getNativeCache();
    Long count = nativeCache.size();
    nativeCache.invalidateAll();
    return count;
  }

  @Override
  public CacheManager getCacheManager() {
    return cacheManager;
  }

  @Override
  public CacheStats computeStats(String name) {

    Cache cache = (Cache) cacheManager.getCache(name).getNativeCache();
    com.google.common.cache.CacheStats stats = cache.stats();
    return builder()
        .name(name)
        .type("Guava Cache")
        .numberOfEntries(cache.size())
        .hitCount(stats.hitCount())
        .hitRate(stats.hitRate())
        .missCount(stats.missCount())
        .missRate(stats.missRate())
        .averageLoadPenalty(stats.averageLoadPenalty())
        .requestCount(stats.requestCount())
        .build();
    }
}
