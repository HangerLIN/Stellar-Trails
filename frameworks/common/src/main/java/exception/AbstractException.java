package exception;

import error.IErrorCode;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Optional;

/**
 * @Classname AbstractException
 * @Description
 * @Date 2023/10/27 17:00
 * @Created by lth
 */
@Data
public class AbstractException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    public final String code;

    public final String message;

    public AbstractException(IErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.code = errorCode.code();
        this.message = Optional.ofNullable(StringUtils.hasLength(message) ? message : cause.getMessage()).orElse(errorCode.message());
    }
}
