package org.example.ray;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @create ${DATE}
 * @author zhoulei
 * @description:
 */
@SpringBootApplication(scanBasePackages = "org.example.ray")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}