package error;

/**
 * @Classname IErrorCode
 * @Description 错误码接口
 * @Date 2023/10/27 17:00
 * @Created by lth
 */
public interface IErrorCode {

    /**
     * 错误码
     */
    String code();

    /**
     * 错误信息
     */
    String message();
}