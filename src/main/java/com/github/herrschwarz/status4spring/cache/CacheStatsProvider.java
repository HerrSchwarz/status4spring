package com.github.herrschwarz.status4spring.cache;

import java.util.List;

public interface CacheStatsProvider {

    public List<CacheStats> getCacheStats();

}
