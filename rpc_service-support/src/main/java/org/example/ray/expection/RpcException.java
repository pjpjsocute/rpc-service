package org.example.ray.expection;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
public class RpcException extends RuntimeException{
    private static final long serialVersionUID = 3707406641232564556L;
    private final String errorCode;

    private final String errorMessage;
    public RpcException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public RpcException(Integer errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = String.valueOf(errorCode);
        this.errorMessage = errorMessage;
    }

    public RpcException(String errorCode, String errorMessage, String originalError) {
        super(originalError);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public RpcException(String errorCode, String errorMessage, String exceptionMessage, Throwable e) {
        super(exceptionMessage, e);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
