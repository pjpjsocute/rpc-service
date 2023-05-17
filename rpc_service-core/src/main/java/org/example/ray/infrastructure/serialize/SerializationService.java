package org.example.ray.infrastructure.serialize;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
public interface SerializationService {

    Byte getSerializationMethod();

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
