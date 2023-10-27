package exception;

import error.IErrorCode;
import lombok.RequiredArgsConstructor;

/**
 * @Classname ClientException
 * @Description 客户端异常
 * @Date 2023/10/27 17:00
 * @Created by lth
 */
@RequiredArgsConstructor
public class ClientException extends AbstractException {

        private static final long serialVersionUID = 1L;

        public ClientException(IErrorCode errorCode, String message, Throwable cause) {
            super(errorCode, message, cause);
        }

        public ClientException(IErrorCode errorCode, String message) {
            super(errorCode, message, null);
        }

        public ClientException(IErrorCode errorCode) {
            super(errorCode, null, null);
        }
}

