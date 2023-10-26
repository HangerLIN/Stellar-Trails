package cache;

import function.CacheGetFilter;
import function.CacheGetIfAbsent;
import function.CacheLoader;
import jakarta.validation.constraints.NotNull;

import java.util.concurrent.TimeUnit;

public interface DistributedCache extends Cache {
    <T> T get(@NotNull String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit);

    <T> T safeGet(@NotNull String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit);

    <T> T safeGet(@NotNull String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter);

    <T> T safeGet(@NotNull String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheCheckFilter);

    <T> T safeGet(@NotNull String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheCheckFilter, CacheGetIfAbsent<String> cacheGetIfAbsent);



}