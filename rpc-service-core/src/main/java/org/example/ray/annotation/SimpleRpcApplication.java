package org.example.ray.annotation;

import java.lang.annotation.*;

import org.example.ray.infrastructure.spring.registrar.CustomBeanScannerRegistrar;
import org.springframework.context.annotation.Import;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description: custom bean scanner
 */

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Import(CustomBeanScannerRegistrar.class)
@Documented
public @interface SimpleRpcApplication {

    String[] basePackage();
}
