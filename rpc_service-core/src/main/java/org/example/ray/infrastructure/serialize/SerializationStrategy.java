package org.example.ray.infrastructure.serialize;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
@Component
@Slf4j
public class SerializationStrategy implements ApplicationContextAware {

    private ApplicationContext              applicationContext;

    private Map<Byte, SerializationService> serviceMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        initStrategy();
    }

    private void initStrategy() {
        Map<String, SerializationService> beanMap = applicationContext.getBeansOfType(SerializationService.class);
        this.serviceMap = beanMap.values()
            .stream()
            .collect(Collectors.toMap(SerializationService::getSerializationMethod, Function.identity()));
    }

    /**
     * serialize
     *
     * @param obj obj
     * @return byte[]
     */
    public byte[] serialize(Object obj, Byte serializationMethod) {

        return findService(serializationMethod).serialize(obj);
    }

    /**
     * deserialize
     *
     * @param bytes
     * @param clazz}
     * @return 反序列化的对象
     */
    public <T> T deserialize(byte[] bytes, Class<T> clazz, Byte serializationMethod) {
        return findService(serializationMethod).deserialize(bytes, clazz);
    }

    private SerializationService findService(Byte serializationMethod) {
        SerializationService serializationService = serviceMap.get(serializationMethod);
        if (serializationService == null) {
            log.error("SerializationService is null,{}", serializationMethod);
            throw new RuntimeException("serializationService is null");
        }
        return serializationService;
    }
}
