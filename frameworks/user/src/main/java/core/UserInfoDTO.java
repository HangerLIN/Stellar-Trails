package core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lth
 * @version 1.0
 * @description 传输层的用户信息
 * @date 2023/10/26 14:51
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfoDTO {
    private String userId;
    private String username;
    private String realName;
    private String token;
}
