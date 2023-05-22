package org.example.ray.infrastructure.netty.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
@Component
public class NettyServerRunner implements ApplicationRunner {

    @Autowired
    private NettyServer nettyServer;

    public NettyServerRunner() {}

    @Override
    public void run(ApplicationArguments args) throws Exception {
        nettyServer.start();
    }
}