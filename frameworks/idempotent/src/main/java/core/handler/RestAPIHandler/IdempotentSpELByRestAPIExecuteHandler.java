package core.handler.RestAPIHandler;

import annotation.Idempotent;
import core.aspect.IdempotentAspect;
import core.context.IdempotentContext;
import core.handler.AbstractIdempotentExecuteHandler;
import core.service.IdempotentSpELService;
import core.wrapper.IdempotentParamWrapper;
import exception.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import toolkits.SpELUtil;

/**
 * @Classname IdempotentSpELByRestAPIExecuteHandler
 * @Description
 * @Date 2023/11/17 12:14
 * @Created by lth
 */
@RequiredArgsConstructor
public class IdempotentSpELByRestAPIExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentSpELService {

    private final RedissonClient redissonClient;

    private final static String LOCK = "lock:spEL:restAPI";


    /**
     * @param joinPoint
     * @return
     */
    @Override
    @SneakyThrows //使用了lombok的注解，可以不用手动抛出异常，标识该方法可能会抛出异常
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        Idempotent idempotent = IdempotentAspect.getIdempotent(joinPoint);
        String key = (String) SpELUtil.parseKey(idempotent.key(), ((MethodSignature) joinPoint.getSignature()).getMethod(), joinPoint.getArgs());
        return IdempotentParamWrapper.builder().lockKey(key).joinPoint(joinPoint).build();
    }

    /**
     * Handles the idempotent parameter wrapper.
     *
     * @param wrapper The IdempotentParamWrapper object to be handled.
     */
    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        String uniqueKey = wrapper.getIdempotent().uniqueKeyPrefix() + wrapper.getLockKey();
        RLock lock = redissonClient.getLock(uniqueKey);
        if (!lock.tryLock()) {
            throw new ClientException(wrapper.getIdempotent().message());
        }
        IdempotentContext.put(LOCK, lock);
    }

    @Override
    public void postProcessing() {
        RLock lock = null;
        try {
            lock = (RLock) IdempotentContext.getKey(LOCK);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }

    @Override
    public void exceptionProcessing() {
        RLock lock = null;
        try {
            lock = (RLock) IdempotentContext.getKey(LOCK);
        } finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }
}
