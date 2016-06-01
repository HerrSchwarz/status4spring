package com.github.herrschwarz.status4spring.cache;

import org.springframework.cache.CacheManager;

import java.util.List;

import static java.util.stream.Collectors.toList;

public interface CacheStatsProvider {

    CacheStats computeStats(String name);
    Long clearCache(String name);
    CacheManager getCacheManager();

    default List<CacheStats> getCacheStats() {
        return getCacheManager().getCacheNames()
                .stream()
                .map(this::computeStats)
                .collect(toList());
    }

}
