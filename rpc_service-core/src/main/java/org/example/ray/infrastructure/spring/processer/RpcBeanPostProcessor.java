package org.example.ray.infrastructure.spring.processer;

import java.lang.reflect.Field;

import org.example.ray.annotation.RpcConsumer;
import org.example.ray.annotation.RpcProvider;
import org.example.ray.domain.RpcServiceConfig;
import org.example.ray.domain.enums.RpcRequestSendingEnum;
import org.example.ray.infrastructure.adapter.RpcSendingServiceAdapter;
import org.example.ray.infrastructure.adapter.RpcServiceRegistryAdapter;
import org.example.ray.infrastructure.adapter.impl.RpcServiceRegistryAdapterImpl;
import org.example.ray.infrastructure.factory.SingletonFactory;
import org.example.ray.infrastructure.proxy.RpcServiceProxy;
import org.example.ray.infrastructure.spi.ExtensionLoader;
import org.example.ray.util.LogUtil;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description: bean processors for custom registration and proxy and injection
 *               of consumers
 */
@Component
public class RpcBeanPostProcessor implements BeanPostProcessor {

    private final RpcServiceRegistryAdapter adapter;

    private final RpcSendingServiceAdapter  sendingServiceAdapter;

    public RpcBeanPostProcessor() {
        this.adapter = SingletonFactory.getInstance(RpcServiceRegistryAdapterImpl.class);;
        this.sendingServiceAdapter = ExtensionLoader.getExtensionLoader(RpcSendingServiceAdapter.class)
            .getExtension(RpcRequestSendingEnum.NETTY.getName());
    }

    /**
     * register service
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        LogUtil.info("start process register service: {}", bean);
        // register service
        if (bean.getClass().isAnnotationPresent(RpcProvider.class)) {
            RpcProvider annotation = bean.getClass().getAnnotation(RpcProvider.class);
            // build rpc service config
            RpcServiceConfig serviceConfig = RpcServiceConfig.builder()
                .service(bean)
                .project(annotation.project())
                .version(annotation.version())
                .group(annotation.group())
                .build();
            LogUtil.info("register service: {}", serviceConfig);
            adapter.registryService(serviceConfig);
        }
        return bean;
    }

    /**
     * proxy and injection of consumers
     *
     * @param bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> toBeProcessedBean = bean.getClass();
        Field[] declaredFields = toBeProcessedBean.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.isAnnotationPresent(RpcConsumer.class)) {
                RpcConsumer annotation = declaredField.getAnnotation(RpcConsumer.class);
                // build rpc service config
                RpcServiceConfig serviceConfig = RpcServiceConfig.builder()
                    .project(annotation.project())
                    .version(annotation.version())
                    .group(annotation.group())
                    .build();
                // create the proxy bean Factory and the proxy bean
                RpcServiceProxy proxy = new RpcServiceProxy(sendingServiceAdapter, serviceConfig);
                Object rpcProxy = proxy.getProxy(declaredField.getType());
                declaredField.setAccessible(true);
                try {
                    LogUtil.info("create service proxy: {}", bean);
                    declaredField.set(bean, rpcProxy);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return bean;
    }
}
