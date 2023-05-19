package org.example.ray.consumer;

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
@SpringBootApplication(scanBasePackages = "org.example.ray.consumer")
@SimpleRpcApplication(basePackage = "org.example.ray.consumer")
public class ConsumerMain {
    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(ConsumerMain.class, args);

    }
}