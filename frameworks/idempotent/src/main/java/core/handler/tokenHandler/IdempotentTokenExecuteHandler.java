package core.handler.tokenHandler;

import cache.DistributedCache;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Strings;
import config.IdempotentProperties;
import core.handler.AbstractIdempotentExecuteHandler;
import core.service.IdempotentSpELService;
import core.service.IdempotentTokenService;
import core.wrapper.IdempotentParamWrapper;
import error.BaseErrorCode;
import exception.ClientException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;
import java.util.UUID;

/**
 * @Classname IdempotentTokenExecute
 * @Description
 * @Date 2023/11/17 15:33
 * @Created by lth
 */
@RequiredArgsConstructor
public class IdempotentTokenExecuteHandler extends AbstractIdempotentExecuteHandler implements IdempotentTokenService {

    private final DistributedCache distributedCache;

    private final IdempotentProperties idempotentProperties;

    private static final String TOKEN_KEY = "token";

    private static final long TOKEN_EXPIRED_TIME = 6000;

    private static final String TOKEN_PREFIX_KEY = "idempotent:token:";

    /**
     * This method is used to create a unique token.
     * The token is created by concatenating a prefix and a randomly generated UUID.
     * The prefix is obtained from the idempotent properties, if it's not null, otherwise a default prefix is used.
     * The token is then stored in the distributed cache with an expiration time.
     * The expiration time is obtained from the idempotent properties, if it's not null, otherwise a default expiration time is used.
     * The method returns the created token.
     *
     * @return The created token.
     */
    public String createToken() {
        // 尝试从 idempotentProperties 获取前缀，如果为空则使用默认的 TOKEN_PREFIX_KEY
        String token = Optional.ofNullable(Strings.emptyToNull(idempotentProperties.getPrefix()))
                .orElse(TOKEN_PREFIX_KEY) + UUID.randomUUID(); // 在前缀后添加一个随机生成的UUID

        // 将生成的 token 放入分布式缓存中，设置一个过期时间
        // 过期时间从 idempotentProperties 中获取，如果未设置则使用默认的 TOKEN_EXPIRED_TIME
        distributedCache.put(token, "", Optional.ofNullable(idempotentProperties.getTimeout()).orElse(TOKEN_EXPIRED_TIME));

        // 返回生成的 token
        return token;
    }

    /**
     * 校验Token
     *
     * @param token
     * @return
     */
    @Override
    public boolean checkToken(String token) {
        return false;
    }

    /**
     * 删除Token
     *
     * @param token
     */
    @Override
    public void deleteToken(String token) {

    }


    /**
     * @param joinPoint
     * @return
     */
    @Override
    protected IdempotentParamWrapper buildWrapper(ProceedingJoinPoint joinPoint) {
       return new IdempotentParamWrapper();
    }

    /**
     * This method handles the idempotent parameter wrapper.
     * It first retrieves the token from the request header. If the token is not found in the header, it tries to get it from the request parameters.
     * If the token is still not found, it throws a ClientException with an error code indicating that the idempotent token is null.
     * Then, it tries to delete the token from the distributed cache. If the deletion fails, it throws a ClientException with an error message and an error code indicating that the idempotent token deletion failed.
     *
     * @param wrapper The IdempotentParamWrapper object to be handled.
     * @throws ClientException if the token is not found or if the token deletion fails.
     */
    @Override
    public void handler(IdempotentParamWrapper wrapper) {
        HttpServletRequest request = ((ServletRequestAttributes) (RequestContextHolder.currentRequestAttributes())).getRequest();
        if (request == null) {
            throw new RuntimeException("request is null");
        }
        String token = request.getHeader(TOKEN_KEY);
        if (StrUtil.isBlank(token)){
            token = request.getParameter(TOKEN_KEY);
            if (StrUtil.isBlank(token)){
                throw new ClientException("token is null");
            }
        }

        Boolean tokenDelFlag = distributedCache.delete(token);
        if (!tokenDelFlag) {
            // 如果删除失败，构造错误消息并抛出异常
            String errMsg = StrUtil.isNotBlank(wrapper.getIdempotent().message())
                    ? wrapper.getIdempotent().message()
                    : BaseErrorCode.IDEMPOTENT_TOKEN_DELETE_ERROR.message();
            throw new ClientException(BaseErrorCode.IDEMPOTENT_TOKEN_DELETE_ERROR, errMsg);
        }
    }
}
