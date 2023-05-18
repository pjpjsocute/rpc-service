package org.example.ray.infrastructure.serialize.impl;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;
import org.example.ray.provider.domain.enums.SerializationTypeEnum;
import org.example.ray.expection.RpcException;
import org.example.ray.infrastructure.serialize.SerializationService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.example.ray.provider.domain.enums.RpcErrorMessageEnum.SERIALIZATION_FAILURE;

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
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()) {
            HessianOutput hessianOutput = new HessianOutput(byteArrayOutputStream);
            hessianOutput.writeObject(obj);

            return byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            throw new RpcException(SERIALIZATION_FAILURE.getCode(),SERIALIZATION_FAILURE.getMessage());
        }

    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {

        try (ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes)) {
            HessianInput hessianInput = new HessianInput(byteArrayInputStream);
            Object o = hessianInput.readObject();

            return clazz.cast(o);

        } catch (Exception e) {
            throw new RpcException(SERIALIZATION_FAILURE.getCode(),SERIALIZATION_FAILURE.getMessage());
        }

    }
}
