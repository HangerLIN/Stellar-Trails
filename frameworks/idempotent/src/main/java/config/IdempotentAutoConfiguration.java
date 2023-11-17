package config;

import cache.DistributedCache;
import core.aspect.IdempotentAspect;
import core.handler.IdempotentExecuteHandler;
import core.handler.MQHandler.IdempotentSpELByMQExecuteHandler;
import core.handler.RestAPIHandler.IdempotentSpELByRestAPIExecuteHandler;
import core.handler.paramHandler.IdempotentParamExecuteHandler;
import core.handler.tokenHandler.IdempotentTokenController;
import core.handler.tokenHandler.IdempotentTokenExecuteHandler;
import core.service.IdempotentSpELService;
import core.service.IdempotentTokenService;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * @Description 幂等核心组件的自动装配
 * @data 2023/11/17 16:15
 * @author lth
 */
@EnableConfigurationProperties(IdempotentProperties.class)
public class IdempotentAutoConfiguration {

    /**
     * 幂等切面
     */
    @Bean
    public IdempotentAspect idempotentAspect() {
        return new IdempotentAspect();
    }

    /**
     * 参数方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentExecuteHandler idempotentParamExecuteHandler(RedissonClient redissonClient) {
        return new IdempotentParamExecuteHandler(redissonClient);
    }

    /**
     * Token 方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentTokenService idempotentTokenExecuteHandler(DistributedCache distributedCache,
                                                                IdempotentProperties idempotentProperties) {
        return new IdempotentTokenExecuteHandler(distributedCache, idempotentProperties);
    }

    /**
     * 申请幂等 Token 控制器，基于 RestAPI 场景
     */
    @Bean
    public IdempotentTokenController idempotentTokenController(IdempotentTokenService idempotentTokenService) {
        return new IdempotentTokenController(idempotentTokenService);
    }

    /**
     * SpEL 方式幂等实现，基于 RestAPI 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentSpELService idempotentSpELByRestAPIExecuteHandler(RedissonClient redissonClient) {
        return new IdempotentSpELByRestAPIExecuteHandler(redissonClient);
    }

    /**
     * SpEL 方式幂等实现，基于 MQ 场景
     */
    @Bean
    @ConditionalOnMissingBean
    public IdempotentSpELByMQExecuteHandler idempotentSpELByMQExecuteHandler(DistributedCache distributedCache) {
        return new IdempotentSpELByMQExecuteHandler(distributedCache);
    }
}