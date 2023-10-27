package exception;

import error.BaseErrorCode;
import error.IErrorCode;

public class RemoteException extends AbstractException {

    public RemoteException(String message) {
        this(message, null, BaseErrorCode.REMOTE_ERROR);
    }

    public RemoteException(String message, IErrorCode errorCode) {
        this(message, null, errorCode);
    }

    public RemoteException(String message, Throwable throwable, IErrorCode errorCode) {
        super(errorCode, message, throwable);
    }

    @Override
    public String toString() {
        return "RemoteException{" +
                "code='" + code + "'," +
                "message='" + message + "'" +
                '}';
    }
}