package org.example.ray.circuitbreaker.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author zhoulei
 * @create 2024/6/5
 * @description:
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface CircuitBreakerEnable {

    String name();

    int failureThreshold();

    int successThreshold();

    int failureDuration();
}
