package com.github.herrschwarz.status4spring.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.concurrent.ConcurrentHashMap;

import static com.github.herrschwarz.status4spring.cache.ImmutableCacheStats.builder;

public class ConcurrentMapCacheStatsProvider implements CacheStatsProvider {

  private ConcurrentMapCacheManager cacheManager;
  private static final String TYPE = "Concurrent map cache";

  public ConcurrentMapCacheStatsProvider(ConcurrentMapCacheManager cacheManager) {
    this.cacheManager = cacheManager;
  }

  @Override
  public Long clearCache(String name) {
    Cache cache = cacheManager.getCache(name);
    Long count = ((ConcurrentHashMap) cache.getNativeCache()).mappingCount();
    cache.clear();
    return count;
  }

  @Override
  public CacheManager getCacheManager() {
    return cacheManager;
  }

  @Override
  public CacheStats computeStats(String name) {
    ConcurrentMapCache cache = (ConcurrentMapCache) cacheManager.getCache(name);
    Long count = ((ConcurrentHashMap) cache.getNativeCache()).mappingCount();
    return builder()
        .name(name)
        .type(TYPE)
        .numberOfEntries(count)
        .build();
  }
}
