package com.hanger.cache;

import cache.StringRedisTemplateProxy;
import config.BloomFilterProperties;
import config.RedisDistributedProperties;
import lombok.AllArgsConstructor;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import utils.RedisKeySerializer;

/**
 * @description: 缓存配置自动装配
 */
@AllArgsConstructor
@EnableConfigurationProperties({RedisDistributedProperties.class, BloomFilterProperties.class})
public class CacheAutoApplication {

    private final RedisDistributedProperties redisDistributedProperties;

    /**
     * 创建 Redis Key 序列化器，可自定义 Key Prefix
     * @return
     */
   @Bean
   @ConditionalOnMissingBean
    public RedisKeySerializer redisKeySerializer() {
      String prefix = redisDistributedProperties.getPrefix();
      String prefixCharset = redisDistributedProperties.getPrefixCharset();
      return new RedisKeySerializer(prefix, prefixCharset);
   }

    /**
     * 防止缓存穿透的布隆过滤器
     */
    @Bean
    @ConditionalOnProperty(prefix = "framework.cache.redis.bloom-filter.default", name = "enabled", havingValue = "true")
    public RBloomFilter<String> cachePenetrationBloomFilter(RedissonClient redissonClient, BloomFilterProperties bloomFilterProperties) {
        RBloomFilter<String> cachePenetrationBloomFilter = redissonClient.getBloomFilter(bloomFilterProperties.getName());
        cachePenetrationBloomFilter.tryInit(bloomFilterProperties.getExpectedInsertions(), bloomFilterProperties.getFalseProbability());
        return cachePenetrationBloomFilter;
    }

    @Bean
    public StringRedisTemplateProxy stringRedisTemplateProxy(RedisKeySerializer redisKeySerializer, StringRedisTemplate stringRedisTemplate, RedissonClient redissonClient){
        stringRedisTemplate.setKeySerializer(redisKeySerializer);
        return new StringRedisTemplateProxy(stringRedisTemplate, redisDistributedProperties, redissonClient);
    }
}
