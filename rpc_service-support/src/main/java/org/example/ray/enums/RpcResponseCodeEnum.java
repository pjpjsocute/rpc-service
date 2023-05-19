package org.example.ray.enums;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
public enum RpcResponseCodeEnum {
    /**
     * success
     */
    SUCCESS(200, "success"),

    /**
     * fail
     */
    FAIL(500, "fail");

    /**
     * code
     */
    private final Integer code;

    /**
     * message
     */
    private final String message;

    RpcResponseCodeEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
