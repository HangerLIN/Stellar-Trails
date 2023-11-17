package core.service;

/**
 * @Classname IdempotentTokenService
 * @Description
 * @Date 2023/11/16 16:49
 * @Created by lth
 */
public interface IdempotentTokenService extends IdempotentSpELService {

        /**
        * 创建Token
        *
        * @return
        */
        String createToken();

        /**
        * 校验Token
        *
        * @param token
        * @return
        */
        boolean checkToken(String token);

        /**
        * 删除Token
        *
        * @param token
        */
        void deleteToken(String token);
}
