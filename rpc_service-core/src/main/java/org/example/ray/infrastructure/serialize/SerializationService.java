package org.example.ray.infrastructure.serialize;

import org.example.ray.infrastructure.spi.SPI;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
@SPI
public interface SerializationService {

    /**
     * serialize
     *
     * @param obj obj
     * @return byte[]
     */
    byte[] serialize(Object obj);

    /**
     * deserialize
     *
     * @param bytes
     * @param clazz}
     * @return 反序列化的对象
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);

}
