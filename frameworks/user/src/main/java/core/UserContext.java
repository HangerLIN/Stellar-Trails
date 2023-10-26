package core;

import com.alibaba.ttl.TransmittableThreadLocal;

import java.util.Optional;

/**
 * @author lth
 * @version 1.0
 * @description 用户上下文
 * @date 2023/10/26 14:50
 */
public final class UserContext {
    private static final ThreadLocal<UserInfoDTO> USER_INFO = new TransmittableThreadLocal<>();

    public static void setUser(UserInfoDTO user) {
        USER_INFO.set(user);
    }

    public static String getUserId() {
        UserInfoDTO userInfoDTO = USER_INFO.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getUserId).orElse(null);
    }

    public static String getUserName() {
        UserInfoDTO userInfoDTO = USER_INFO.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getUsername).orElse(null);
    }

    public static String getRealName() {
        UserInfoDTO userInfoDTO = USER_INFO.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getRealName).orElse(null);
    }

    public static String getToken() {
        UserInfoDTO userInfoDTO = USER_INFO.get();
        return Optional.ofNullable(userInfoDTO).map(UserInfoDTO::getToken).orElse(null);
    }

    public static void setUserInfo(UserInfoDTO userInfoDTO) {
        USER_INFO.set(userInfoDTO);
    }

    public static void removeUserInfo() {
        USER_INFO.remove();
    }
}
