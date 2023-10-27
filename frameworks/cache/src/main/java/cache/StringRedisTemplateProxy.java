package cache;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import config.RedisDistributedProperties;
import function.CacheGetFilter;
import function.CacheGetIfAbsent;
import function.CacheLoader;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.scripting.support.ResourceScriptSource;
import singleton.Singleton;
import utils.CacheUtil;
import utils.FastJson2Util;

import java.util.Collection;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public class StringRedisTemplateProxy implements DistributedCache {

    private final StringRedisTemplate stringRedisTemplate;
    private final RedisDistributedProperties redisProperties;
    private final RedissonClient redissonClient;

    private static final String LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH = "lua/putIfAllAbsent.lua";
    private static final String SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX = "safe_get_distributed_lock_get:";

    /**
     * 获取缓存
     *
     * @param key
     * @param clazz
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        String res = stringRedisTemplate.opsForValue().get(key);
        //获取的内容一定是序列化后的
        if (!String.class.isAssignableFrom(clazz)){
            //防止嵌套！内层的代码进行复杂的格式，这个将能把JSON格式的进行饭序列化
            res = JSON.parseObject(res, FastJson2Util.buildType(clazz));
        }
        return (T) res;
    }

    /**
     * 放入缓存
     *
     * @param key
     * @param value
     * @param valueTimeout
     */
    @Override
    public void put(String key, Object value, Long valueTimeout) {
        put( key,  value, redisProperties.getValueTimeout());
    }

    /**
     * 如果 keys 全部不存在，则新增，返回 true，反之 false
     * @param keys
     */
    @Override
    public Boolean putIfAllAbsent(Collection<String> keys) {
        DefaultRedisScript<Boolean> actual = Singleton.get(LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH, () -> {
            DefaultRedisScript redisScript = new DefaultRedisScript();
            redisScript.setScriptSource(new ResourceScriptSource(new ClassPathResource(LUA_PUT_IF_ALL_ABSENT_SCRIPT_PATH)));
            redisScript.setResultType(Boolean.class);
            return redisScript;
        });
        //为了保证同时放入多个元素是一个原子性的操作，所以使用lua脚本自定义适应；下面使用Guava库的来创建一个列表，其中这个链表是能够改变的；butJava9内部的List.of生成的是一个不可变的列表，为了能够进行修改，需要改成别的
        //Boolean res = stringRedisTemplate.execute(actual, Lists.newArrayList(keys),redisProperties.getValueTimeout().toString());

        List<String> keyList = List.of(keys.toString());
        Boolean res = stringRedisTemplate.execute(actual, keyList, redisProperties.getValueTimeout().toString());
        return res != null && res;
    }

    /**
     * 删除缓存
     *
     * @param key
     */
    @Override
    public Boolean delete(String key){
        return stringRedisTemplate.delete(key);
    }

    /**
     * 删除 keys，返回删除数量
     *
     * @param keys
     */
    @Override
    public Long delete(Collection<String> keys) {
        return stringRedisTemplate.delete(keys);
    }

    /**
     * 判断 key 是否存在
     *
     * @param key
     */
    @Override
    public Boolean hasKey(String key) {
        return stringRedisTemplate.hasKey(key);
    }

    /**
     * 获取缓存组件实例
     */
    @Override
    public Object getInstance() {
        return stringRedisTemplate;
    }

    /**
     * @param key
     * @param clazz
     * @param cacheLoader
     * @param timeout
     * @param timeUnit
     * @param <T>
     * @return
     */
    @Override
    public <T> T get(String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit) {
        T res = get(key, clazz);
        if (!CacheUtil.isNullOrBlank(res)) {
            return res;
        }
        return loadAndSet(key, cacheLoader, timeout, timeUnit, false, null, false);
    }

    /**
     * @param key
     * @param clazz
     * @param cacheLoader
     * @param timeout
     * @param timeUnit
     * @param <T>
     * @return
     */
    @Override
    public <T> T safeGet(String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit) {
        return safeGet(key, clazz, cacheLoader, timeout, timeUnit, null);
    }

    /**
     * @param key
     * @param clazz
     * @param cacheLoader
     * @param timeout
     * @param timeUnit
     * @param bloomFilter
     * @param <T>
     * @return
     */
    @Override
    public <T> T safeGet(String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter) {
       return safeGet(key, clazz, cacheLoader, timeout, timeUnit, bloomFilter, null);
    }


    /**
     * @param key
     * @param clazz
     * @param cacheLoader
     * @param timeout
     * @param timeUnit
     * @param bloomFilter
     * @param cacheCheckFilter
     * @param <T>
     * @return
     */
    @Override
    public <T> T safeGet(String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheCheckFilter) {
        return safeGet(key, clazz, cacheLoader, timeout, timeUnit, bloomFilter, cacheCheckFilter, null);
    }

    /**
     * @param key
     * @param clazz
     * @param cacheLoader
     * @param timeout
     * @param timeUnit
     * @param bloomFilter
     * @param cacheCheckFilter
     * @param <T>
     * @return
     */
    @Override
    public <T> T safeGet(String key, Class<T> clazz, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter, CacheGetFilter<String> cacheCheckFilter, CacheGetIfAbsent<String> cacheGetIfAbsent) {
        T res = get(key, clazz);
        //已经有一个有效的缓存值。
        //业务逻辑决定应该接受并返回当前的缓存值。
        //布隆过滤器告诉我们这个key绝对不在缓存中，所以不需要再查找或加载。
        //其中，后面的两个条件是为了防止缓存穿透，而设置的，里面很有可能是一个空值或者是自己设计的null值
        if (!CacheUtil.isNullOrBlank(res) || Optional.ofNullable(cacheCheckFilter).map(each -> each.filter(key)).orElse(false) || Optional.ofNullable(bloomFilter).map(each -> !each.contains(key)).orElse(false)) {
            return res;
        }
        RLock lock = redissonClient.getLock(SAFE_GET_DISTRIBUTED_LOCK_KEY_PREFIX + key);
        lock.lock();
        try {
            if (CacheUtil.isNullOrBlank(res = get(key, clazz))) {
                res = loadAndSet(key, cacheLoader, timeout, timeUnit, true, bloomFilter,false);
                if (res == null) {
                   Optional.ofNullable(cacheGetIfAbsent).ifPresent(each -> each.execute(key));
                }
            }
        }finally {
            lock.unlock();
        }
        return res;
    }


    /**
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     */
    @Override
    public void put(String key, Object value, long timeout, TimeUnit timeUnit) {
        //是否需要序列化
        String res = value instanceof String ? (String) value : JSON.toJSONString(value);
        stringRedisTemplate.opsForValue().set(key, res, timeout, timeUnit);
    }

    /**
     * @param key
     * @param value
     * @param timeout
     * @param timeUnit
     * @param bloomFilter
     */
    @Override
    public <T> T safeGet(@NotNull String key, Object value, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter) {
        return safeGet(key, value, timeout, timeUnit, bloomFilter);
    }

    /**
    @Description: 从数据库加载数据到缓存，这部分也封装了一份策略！
     * @param key
     * @param cacheLoader
     * @param timeout
     * @param timeUnit
     * @param isSetIfAbsent
     * @param bloomFilter
     * @param safeFlag
     * @return
     * @param <T>
     */
    @Override
    public <T> T loadAndSet(@NotNull String key, CacheLoader<T> cacheLoader, long timeout, TimeUnit timeUnit, boolean isSetIfAbsent, RBloomFilter<String> bloomFilter, boolean safeFlag) {
        T res = cacheLoader.load();
        if (CacheUtil.isNullOrBlank(res)) {
            return null;//说明数据源数据存在问题，直接返回null
        }
        if (safeFlag) safePut(key, res, timeout, timeUnit, bloomFilter);
        else put(key, res, timeout, timeUnit);

        return res;
    }

    @Override
    public void safePut(String key, Object value, long timeout, TimeUnit timeUnit, RBloomFilter<String> bloomFilter) {
        put(key, value, timeout, timeUnit);
        if (bloomFilter != null) {
            bloomFilter.add(key);
        }
    }

    /**
     * 统计指定key的数量
     *
     * @param keys
     */
    @Override
    public Long countExistingKeys(@NotNull String... keys) {
        var list = Lists.newArrayList(keys);
        return stringRedisTemplate.countExistingKeys(list);
    }
}