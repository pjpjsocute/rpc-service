package org.example.ray.domain.enums;

import lombok.Getter;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
public enum CompressTypeEnum {

    GZIP((byte) 0x01, "gzip");

    @Getter
    private final byte code;
    @Getter
    private final String name;

    CompressTypeEnum(byte code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getName(byte code) {
        for (CompressTypeEnum c : CompressTypeEnum.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return null;
    }

}
