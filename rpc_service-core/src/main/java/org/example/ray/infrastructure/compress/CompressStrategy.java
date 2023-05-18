package org.example.ray.infrastructure.compress;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.example.ray.infrastructure.config.PropertiesReader;
import org.example.ray.infrastructure.factory.SingletonFactory;
import org.example.ray.infrastructure.util.LogUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import javax.annotation.PostConstruct;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
@Component
public class CompressStrategy implements ApplicationContextAware {

    private ApplicationContext         applicationContext;

    private static Map<Byte, CompressService> serviceMap = new ConcurrentHashMap<>();

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        initStrategy();
    }

    private void initStrategy() {
        Map<String, CompressService> beanMap = applicationContext.getBeansOfType(CompressService.class);

        for (CompressService compressService : beanMap.values()) {
            serviceMap.put(compressService.getCompressMethod(), compressService);
        }
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

    public  CompressService findService(Byte serializationMethod) {
        CompressService  compressService= serviceMap.get(serializationMethod);
        if (compressService == null) {
            LogUtil.error("CompressService is null,{}", serializationMethod);
            throw new RuntimeException("compressService is null");
        }
        return serviceMap.get(serializationMethod);
    }

}
