package org.example.ray;

import org.example.ray.annotation.SimpleRpcApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @create ${DATE}
 * @author zhoulei
 * @description:
 */
@SpringBootApplication(scanBasePackages = "org.example.ray")
@SimpleRpcApplication(basePackage = "org.example.ray.application")
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        // AnnotationConfigApplicationContext applicationContext = new
        // AnnotationConfigApplicationContext(Main.class);
        // TestInterface helloController = (TestInterface)
        // applicationContext.getBean("TestInterface");
        //
        // System.out.println(helloController);
    }
}