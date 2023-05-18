package org.example.ray.infrastructure.netty.server;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import org.example.ray.infrastructure.config.ServerShutdownHook;
import org.example.ray.infrastructure.netty.NettyRpcClientHandler;
import org.example.ray.infrastructure.netty.RpcMessageDecoder;
import org.example.ray.infrastructure.netty.RpcMessageEncoder;
import org.example.ray.infrastructure.util.LogUtil;
import org.example.ray.infrastructure.util.ThreadPoolFactoryUtil;
import org.springframework.stereotype.Component;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description: netty server listening the client side and run the rpc service
 */
@Component

public class NettyServer {

    public static final int                 PORT = 9999;
    @Resource
    private NettyRpcClientHandler nettyRpcClientHandler;

    public void start() {
        // first clear the registry
        ServerShutdownHook.getInstance().registerShutdownHook();

        //init listenerGroup,listen to request and relate to workerGroup
       EventLoopGroup listenerGroup = new NioEventLoopGroup(1);
        //init workerGroup,handle the request:I/0
        EventLoopGroup workerGroup = new NioEventLoopGroup(4);
        //group used to for handel the request and response
        DefaultEventExecutorGroup businessGroup = new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("netty-server-business-group", false)
        );;

        //start the server
        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(listenerGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(
                            new ChannelInitializer<SocketChannel>() {
                                @Override
                                protected void initChannel(SocketChannel socketChannel) throws Exception {
                                    ChannelPipeline pipeline = socketChannel.pipeline();
                                    // 30s no read, 60s no write, 100s no read and write, close the connection
                                    pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                                    pipeline.addLast(new RpcMessageEncoder());
                                    pipeline.addLast(new RpcMessageDecoder());
                                    pipeline.addLast(businessGroup, nettyRpcClientHandler);
                                }
                            });

            String host = InetAddress.getLocalHost().getHostAddress();
            // bind port
            ChannelFuture f = serverBootstrap.bind(host, PORT).sync();
            // close
            f.channel().closeFuture().sync();
        }catch (Exception e){
            LogUtil.error("occur exception when start server:", e);
        }finally {
            LogUtil.error("shutdown bossGroup and workerGroup");
            listenerGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            businessGroup.shutdownGracefully();
        }


    }

}
