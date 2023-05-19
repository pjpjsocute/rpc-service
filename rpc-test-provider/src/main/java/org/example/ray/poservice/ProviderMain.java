package org.example.ray.poservice;

import org.example.ray.annotation.SimpleRpcApplication;
import org.example.ray.infrastructure.netty.server.NettyServer;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

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
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(ProviderMain.class);
        NettyServer nettyRpcServer = (NettyServer) applicationContext.getBean("nettyServer");
        nettyRpcServer.start();
        // AnnotationConfigApplicationContext applicationContext = new
        // AnnotationConfigApplicationContext(Main.class);
        // TestInterface helloController = (TestInterface)
        // applicationContext.getBean("TestInterface");
        //
        // System.out.println(helloController);
    }
}