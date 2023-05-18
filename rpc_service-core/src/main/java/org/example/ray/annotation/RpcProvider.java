package org.example.ray.annotation;

import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description: RPC provider that enables automatic injection into spring, registration
 */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface RpcProvider {

    /**
     * Service group, default value is empty string
     */
    String project() default "default";

    /**
     * Service version, default value is 1.0
     * @return
     */
    String version() default "1.0";

    /**
     * Service group, default value is empty string
     */
    String group() default "default";

    String serviceName() ;
}
