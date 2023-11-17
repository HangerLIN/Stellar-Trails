package core.handler.MQHandler;

import annotation.Idempotent;
import cache.DistributedCache;
import core.aspect.IdempotentAspect;
import core.aspect.RepeatConsumptionException;
import core.context.IdempotentContext;
import core.handler.AbstractIdempotentExecuteHandler;
import core.service.IdempotentSpELService;
import core.wrapper.IdempotentParamWrapper;
import enums.IdempotentMQConsumeStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import toolkits.LogUtil;
import toolkits.SpELUtil;

import java.util.concurrent.TimeUnit;

/**
 * 基于 SpEL 方法验证请求幂等性，适用于 MQ 场景
 * @author lth
 * @date 2023/11/16 17:34
 */
@RequiredArgsConstructor
public final class IdempotentSpELByMQExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentSpELService {

    private final DistributedCache distributedCache;

    private final static int TIMEOUT = 600;
    private final static String WRAPPER = "wrapper:spEL:MQ";

    @SneakyThrows
    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint){
        Idempotent idempotent = IdempotentAspect.getIdempotent(joinPoint);
        String key = (String) SpELUtil.parseKey(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
        return IdempotentParamWrapper.builder()
                .lockKey(key)
                .joinPoint(joinPoint)
                .build();
    }

    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        String uniqueKey = wrapper.getIdempotent().uniqueKeyPrefix() + wrapper.getLockKey();
        Boolean setIfAbsent = ((StringRedisTemplate) distributedCache.getInstance())
                .opsForValue()
                .setIfAbsent(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMING.getCode(), TIMEOUT, TimeUnit.SECONDS);
        if (setIfAbsent != null && !setIfAbsent) {
            String consumeStatus = distributedCache.get(uniqueKey, String.class);
            boolean error = IdempotentMQConsumeStatusEnum.isError(consumeStatus);
            LogUtil.getLog(wrapper.getJoinPoint()).warn("[{}] MQ repeated consumption, {}.", uniqueKey, error ? "Wait for the client to delay consumption" : "Status is completed");
            try {
                throw new RepeatConsumptionException(error);
            } catch (RepeatConsumptionException e) {
                throw new RuntimeException(e);
            }
        }
        IdempotentContext.put(WRAPPER, wrapper);
    }

    /**
     * 在handler方法中，首先会尝试将消息的唯一标识和一个表示消息正在被处理的状态值存入缓存。
     * 如果存入成功，说明这个消息还没有被处理过，可以继续处理。如果存入失败，说明这个消息已经被处理过，就会抛出一个RepeatConsumptionException异常。
     * 在exceptionProcessing方法中，如果处理消息时发生了异常，会删除缓存中的这个消息的唯一标识。
     * 这是因为消息处理失败，所以需要删除这个标识，以便消息可以被重新处理。
     * 在postProcessing方法中，如果消息处理成功，会将消息的唯一标识和一个表示消息已经被处理的状态值存入缓存。
     * 这是为了防止消息被重复处理。因为下次再收到这个消息时，会发现缓存中已经有这个消息的唯一标识，就不会再处理这个消息了。
     */
    @Override
    public void exceptionProcessing(){
        IdempotentParamWrapper wrapper = (IdempotentParamWrapper) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            Idempotent idempotent = wrapper.getIdempotent();
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {
                distributedCache.delete(uniqueKey);
            }catch (Throwable ex){
                LogUtil.getLog(wrapper.getJoinPoint()).error("[{}] MQ repeated consumption, {}.", uniqueKey, ex.getMessage());
            }
        }
    }

    @Override
    public void postProcessing(){
        IdempotentParamWrapper wrapper = (IdempotentParamWrapper) IdempotentContext.getKey(WRAPPER);
        if (wrapper != null) {
            Idempotent idempotent = wrapper.getIdempotent();
            String uniqueKey = idempotent.uniqueKeyPrefix() + wrapper.getLockKey();
            try {
                distributedCache.put(uniqueKey, IdempotentMQConsumeStatusEnum.CONSUMED.getCode(), idempotent.keyTimeout(), TimeUnit.SECONDS);
            }catch (Throwable ex){
                LogUtil.getLog(wrapper.getJoinPoint()).error("[{}] MQ repeated consumption, {}.", uniqueKey, ex.getMessage());
            }
        }
    }

}