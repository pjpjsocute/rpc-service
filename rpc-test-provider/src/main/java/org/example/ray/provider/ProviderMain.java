package org.example.ray.provider;

import org.example.ray.annotation.SimpleRpcApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 
 * @create ${DATE}
 * @author zhoulei
 * @description:
 */
@SpringBootApplication(scanBasePackages = "org.example.ray.provider.application")
@SimpleRpcApplication(basePackage = "org.example.ray.provider.application")
public class ProviderMain {
    public static void main(String[] args) {
        SpringApplication.run(ProviderMain.class, args);
        // AnnotationConfigApplicationContext applicationContext = new
        // AnnotationConfigApplicationContext(Main.class);
        // TestInterface helloController = (TestInterface)
        // applicationContext.getBean("TestInterface");
        //
        // System.out.println(helloController);
    }
}