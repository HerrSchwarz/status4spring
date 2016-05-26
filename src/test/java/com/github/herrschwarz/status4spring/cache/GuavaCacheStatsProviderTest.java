package com.github.herrschwarz.status4spring.cache;

import com.google.common.cache.Cache;
import com.google.common.collect.ImmutableList;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.cache.guava.GuavaCache;
import org.springframework.cache.guava.GuavaCacheManager;

import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GuavaCacheStatsProviderTest {
    private static final String CACHE_NAME = "test";
    private static final String CACHE_TYPE = "Guava Cache";
    private static final long NUMER_OF_ENTRIES = 2L;
    private Collection<String> cacheNames = ImmutableList.of(CACHE_NAME);

    @Mock
    private GuavaCacheManager cacheManager;
    @Mock
    private Cache<Object, Object> cache;

    @Before
    public void setUp() {
        GuavaCache guavaCache = new GuavaCache(CACHE_NAME, cache);
        when(cacheManager.getCacheNames()).thenReturn(cacheNames);
        when(cacheManager.getCache(CACHE_NAME)).thenReturn(guavaCache);
        com.google.common.cache.CacheStats stats = new com.google.common.cache.CacheStats(1L, 2L, 3L, 4L, 5L, 6L);
        when(cache.stats()).thenReturn(stats);
        when(cache.size()).thenReturn(NUMER_OF_ENTRIES);
    }

    @Test
    public void shouldSetTheNameOfTheCache() throws Exception {
        // Given
        GuavaCacheStatsProvider statsProvider = new GuavaCacheStatsProvider(cacheManager);

        // When
        CacheStats cacheStats = statsProvider.getCacheStats().get(0);

        // Then
        assertThat(cacheStats.getName(), is(CACHE_NAME));
    }

    @Test
    public void shouldSetTheTypeOfTheCache() throws Exception {
        // Given
        GuavaCacheStatsProvider statsProvider = new GuavaCacheStatsProvider(cacheManager);

        // When
        CacheStats cacheStats = statsProvider.getCacheStats().get(0);

        // Then
        assertThat(cacheStats.getType(), is(CACHE_TYPE));
    }

    @Test
    public void shouldSetTheNumberOfEntriesOfTheCache() throws Exception {
        // Given
        GuavaCacheStatsProvider statsProvider = new GuavaCacheStatsProvider(cacheManager);

        // When
        CacheStats cacheStats = statsProvider.getCacheStats().get(0);

        // Then
        assertThat(cacheStats.getNumberOfEntries(), is(NUMER_OF_ENTRIES));
    }

    @Test
    public void shouldClearCache() throws Exception {
        // Given
        GuavaCacheStatsProvider statsProvider = new GuavaCacheStatsProvider(cacheManager);

        // When
        Long deleted = statsProvider.clearCache("test");

        // Then
        assertThat(deleted, is(2L));
        verify(cache, times(1)).invalidateAll();
    }

    @Test
    public void shouldReturnNumberOfDeletedEntries() throws Exception {
        // Given
        GuavaCacheStatsProvider statsProvider = new GuavaCacheStatsProvider(cacheManager);

        // When
        Long deletedEntries = statsProvider.clearCache("test");

        // Then
        assertThat(deletedEntries, is(2L));
    }

}