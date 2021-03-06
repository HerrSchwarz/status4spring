package com.github.herrschwarz.status4spring.cache;

import com.github.herrschwarz.status4spring.groups.UnitTest;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ConcurrentMapCache.class)
@Category(UnitTest.class)
public class ConcurrentMapCacheStatsProviderTest {

    private static final String CACHE_NAME = "test";
    private static final String CACHE_TYPE = "Concurrent map cache";
    private Collection<String> cacheNames = ImmutableList.of(CACHE_NAME);
    private ConcurrentMap<Object, Object> concurrentHashMap = new ConcurrentHashMap<>();

    @Mock
    private ConcurrentMapCacheManager cacheManager;
    @Mock
    private ConcurrentMapCache cache;

    @Before
    public void setUp() {
        concurrentHashMap.put("entry1", 42);
        concurrentHashMap.put("entry2", 43);
        when(cacheManager.getCacheNames()).thenReturn(cacheNames);
        when(cacheManager.getCache(CACHE_NAME)).thenReturn(cache);
        when(cache.getNativeCache()).thenReturn(concurrentHashMap);
    }

    @Test
    public void shouldSetTheNameOfTheCache() {
        // Given
        ConcurrentMapCacheStatsProvider statsProvider = new ConcurrentMapCacheStatsProvider(cacheManager);

        // When
        CacheStats cacheStats = statsProvider.getCacheStats().get(0);

        // Then
        assertThat(cacheStats.getName(), is(CACHE_NAME));
    }

    @Test
    public void shouldSetTheTypeOfTheCache()  {
        // Given
        ConcurrentMapCacheStatsProvider statsProvider = new ConcurrentMapCacheStatsProvider(cacheManager);

        // When
        CacheStats cacheStats = statsProvider.getCacheStats().get(0);

        // Then
        assertThat(cacheStats.getType(), is(CACHE_TYPE));
    }

    @Test
    public void shouldSetTheNumberOfEntriesOfTheCache() {
        // Given
        ConcurrentMapCacheStatsProvider statsProvider = new ConcurrentMapCacheStatsProvider(cacheManager);

        // When
        CacheStats cacheStats = statsProvider.getCacheStats().get(0);

        // Then
        assertThat(cacheStats.getNumberOfEntries(), is(2L));
    }

    @Test
    public void shouldClearCache() {
        // Given
        ConcurrentMapCacheStatsProvider statsProvider = new ConcurrentMapCacheStatsProvider(cacheManager);

        // When
        Long deleted = statsProvider.clearCache("test");

        // Then
        assertThat(deleted, is(2L));
        verify(cache, times(1)).clear();
    }

    @Test
    public void shouldReturnNumberOfDeletedEntries() {
        // Given
        ConcurrentMapCacheStatsProvider statsProvider = new ConcurrentMapCacheStatsProvider(cacheManager);

        // When
        Long deletedEntries = statsProvider.clearCache("test");

        // Then
        assertThat(deletedEntries, is(2L));
    }

}