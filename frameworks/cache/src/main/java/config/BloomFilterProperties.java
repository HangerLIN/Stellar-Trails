package config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author lth
 * @version 1.0
 * @description TODO
 * @date 2023/10/26 16:01
 */
@Data
@ConfigurationProperties(prefix = "framework.cache.redis.bloom-filter.default")
public class BloomFilterProperties {
//    public static final String PREFIX = ""

    /**
     * 布隆过滤器默认实例名称
     */
    private String name = "cache_penetration_bloom_filter";

    /**
     * 布隆过滤器的预计插入量
     */
    private long expectedInsertions = 1000000L;
    /**
     * 布隆过滤器的误判率
     */
    private double falseProbability = 0.03;
}
