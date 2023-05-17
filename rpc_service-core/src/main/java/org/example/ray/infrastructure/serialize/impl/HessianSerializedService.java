package org.example.ray.infrastructure.serialize.impl;

import org.example.ray.domain.enums.SerializationTypeEnum;
import org.example.ray.infrastructure.serialize.SerializationService;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
public class HessianSerializedService implements SerializationService {
    @Override
    public Byte getSerializationMethod() {
        return SerializationTypeEnum.HESSIAN.getCode();
    }

    @Override
    public byte[] serialize(Object obj) {
        return new byte[0];
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        return null;
    }
}
