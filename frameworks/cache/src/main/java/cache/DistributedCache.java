package cache;

import function.CacheGetFilter;
import function.CacheGetIfAbsent;
import function.CacheLoader;
import jakarta.validation.constraints.NotNull;
import org.redisson.api.RBloomFilter;

import java.util.concurrent.TimeUnit;

public interface DistributedCache extends cache.Cache {
    <T> T get(@NotNull String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit);

    <T> T safeGet(@NotNull String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit);

    <T> T safeGet(@NotNull String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter);

    <T> T safeGet(@NotNull String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheCheckFilter);

    <T> T safeGet(@NotNull String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheCheckFilter, CacheGetIfAbsent<String> cacheGetIfAbsent);

    //放入緩存的操作
    void put(@NotNull String key, Object value, long timeout, TimeUnit timeUnit);

    /**
     *
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     * @param bloomFilter
     */
    <T> T safeGet(@NotNull String key, Object value, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter);

    <T> T loadAndSet(@NotNull String key, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, boolean isSetIfAbsent, RBloomFilter<String> bloomFilter, boolean safeFlag);

    void safePut(String key, Object value, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter);

    /**
     * 统计指定key的数量
     */
    Long countExistingKeys(@NotNull String... keys);
}