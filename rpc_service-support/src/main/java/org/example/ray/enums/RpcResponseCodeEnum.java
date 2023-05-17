package org.example.ray.enums;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
public enum RpcResponseCodeEnum {
    SUCCESS(200, "success"),

    FAIL(500, "fail");;

    private final Integer code;

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
