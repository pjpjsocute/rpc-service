package org.example.ray.poservice;

import org.example.ray.annotation.SimpleRpcApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * 
 * @create ${DATE}
 * @author zhoulei
 * @description:
 */
@SpringBootApplication(scanBasePackages = "org.example.ray.poservice")
@SimpleRpcApplication(basePackage = "org.example.ray.poservice.application")
public class ProviderMain {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ProviderMain.class, args);
    }
}