package org.example.ray.infrastructure.compress;

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
public class CompressStrategy implements ApplicationContextAware {

    private ApplicationContext         applicationContext;

    private Map<Byte, CompressService> serviceMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        initStrategy();
    }

    private void initStrategy() {
        Map<String, CompressService> beanMap = applicationContext.getBeansOfType(CompressService.class);
        this.serviceMap = beanMap.values()
            .stream()
            .collect(Collectors.toMap(CompressService::getCompressMethod, Function.identity()));
    }

    /**
     * compress
     *
     * @param bytes bytes
     * @return byte[]
     */
    public byte[] compress(byte[] bytes, Byte serializationMethod) {
        return findService(serializationMethod).compress(bytes);
    }

    /**
     * decompress
     *
     * @param bytes
     * @return 反序列化的对象
     */
    public byte[] decompress(byte[] bytes, Byte serializationMethod) {
        return findService(serializationMethod).decompress(bytes);
    }

    private CompressService findService(Byte serializationMethod) {
        CompressService compressService = serviceMap.get(serializationMethod);
        if (compressService == null) {
            log.error("CompressService is null,{}", serializationMethod);
            throw new RuntimeException("compressService is null");
        }
        return compressService;
    }
}
