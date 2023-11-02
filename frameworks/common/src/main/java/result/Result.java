package result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Classname Result
 * @Description 统一全局返回结果
 * @Date 2023/10/27 19:24
 * @Created by lth
 */
@Data
@Accessors(chain = true)
public class Result<T> implements Serializable{
    @Serial
    private static final long serialVersionUID = 7844L;

    public static final String SUCCESS_CODE = "0";

    private String code;

    private String message;

    private T data;

    private String requestId;

    public boolean isSuccess() {
        return SUCCESS_CODE.equals(code);
    }

}
