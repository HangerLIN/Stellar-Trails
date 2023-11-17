package core.handler.paramHandler;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson2.JSON;
import core.UserContext;
import core.context.IdempotentContext;
import core.handler.AbstractIdempotentExecuteHandler;
import core.wrapper.IdempotentParamWrapper;
import exception.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


/**
 * @Classname IdempotentParamExecuteHandler
 * @Description
 * @Date 2023/11/17 16:18
 * @Created by lth
 */
@Slf4j
@RequiredArgsConstructor
public class IdempotentParamExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentParamService {

    private final RedissonClient redissonClient;

    private final static String LOCK = "lock:param:restAPI";
    /**
     * @param joinPoint
     * @return
     */
    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
        String lockKey = String.format("idempotent:path:%s:currentUserId:%s:md5:%s", getServletPath(), getCurrentUserId(), calcArgsMD5(joinPoint));
        return IdempotentParamWrapper.builder().lockKey(lockKey).joinPoint(joinPoint).build();
    }

    private String calcArgsMD5(ProceedingJoinPoint joinPoint) {
        String md5 = DigestUtil.md5Hex(JSON.toJSONString(joinPoint.getArgs()));
        return md5;
    }

    private String getCurrentUserId() {
        String userID = UserContext.getUserId();
        if(StrUtil.isBlank(userID)){
            throw new ClientException("用户ID获取失败，请登录");
        }
        return userID;
    }

    private String getServletPath() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return sra.getRequest().getServletPath();
    }

    @Override
    public void postProcessing() {
        RLock lock = null;
        try {
            lock = (RLock) IdempotentContext.getKey(LOCK);
        } catch (RuntimeException e) {
            log.error("An error occurred: ", e);
            throw new RuntimeException();
        }
        finally {
            if (lock != null) {
                lock.unlock();
            }
        }
    }
    /**
     * Handles the idempotent parameter wrapper.
     *
     * @param wrapper The IdempotentParamWrapper object to be handled.
     */
    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        String lockKey = wrapper.getLockKey();
        RLock lock = redissonClient.getLock(lockKey);
        if (!lock.tryLock()) {
            throw new ClientException(wrapper.getIdempotent().message());
        }
        IdempotentContext.put(LOCK, lock);
    }
}
