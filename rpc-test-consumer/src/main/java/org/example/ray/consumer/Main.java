package org.example.ray.consumer;

import org.example.ray.annotation.SimpleRpcApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @create ${DATE}
 * @author zhoulei
 * @description:
 */
@SpringBootApplication(scanBasePackages = "org.example.ray.consumer")
@SimpleRpcApplication(basePackage = "org.example.ray.consumer")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}