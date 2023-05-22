package org.example.ray.infrastructure.spring.registrar;

import org.example.ray.annotation.RpcProvider;
import org.example.ray.annotation.SimpleRpcApplication;
import org.example.ray.infrastructure.spring.scanner.RpcBeanScanner;
import org.example.ray.util.LogUtil;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.stereotype.Component;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description: Custom registrar for scanning and registering annotated beans
 */

public class CustomBeanScannerRegistrar implements ImportBeanDefinitionRegistrar, ResourceLoaderAware {

    private ResourceLoader resourceLoader;

    private static final String API_SCAN_PARAM = "basePackage";

    private static final String SPRING_BEAN_BASE_PACKAGE = "org.example.ray";

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //get the scan annotation and the bean package to be scanned
        String[] scanBasePackages = fetchScanBasePackage(importingClassMetadata);
        LogUtil.info("scanning packages: [{}]", (Object) scanBasePackages);

//        //scan the package and register the bean
//        RpcBeanScanner rpcConsumerBeanScanner = new RpcBeanScanner(registry, RpcConsumer.class);
        RpcBeanScanner rpcProviderBeanScanner = new RpcBeanScanner(registry, RpcProvider.class);
        RpcBeanScanner springBeanScanner = new RpcBeanScanner(registry, Component.class);
        if (resourceLoader != null) {
            springBeanScanner.setResourceLoader(resourceLoader);
            rpcProviderBeanScanner.setResourceLoader(resourceLoader);
        }
        int rpcServiceCount = rpcProviderBeanScanner.scan(scanBasePackages);
        LogUtil.info("rpcServiceScanner扫描的数量 [{}]", rpcServiceCount);
        LogUtil.info("scanning RpcConsumer annotated beans end");
    }

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    private String[] fetchScanBasePackage(AnnotationMetadata importingClassMetadata){
        AnnotationAttributes annotationAttributes = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(SimpleRpcApplication.class.getName()));
        String[] scanBasePackages = new String[0];
        if (annotationAttributes != null) {
            scanBasePackages = annotationAttributes.getStringArray(API_SCAN_PARAM);
        }
        //user doesn't specify the package to scan,use the Application base package
        if (scanBasePackages.length == 0) {
            scanBasePackages = new String[]{((org.springframework.core.type.StandardAnnotationMetadata) importingClassMetadata).getIntrospectedClass().getPackage().getName()};
        }
        return scanBasePackages;
    }

}
