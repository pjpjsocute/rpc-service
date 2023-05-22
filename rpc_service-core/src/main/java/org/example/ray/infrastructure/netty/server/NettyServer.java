package org.example.ray.infrastructure.netty.server;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

import org.example.ray.infrastructure.config.ServerShutdownHook;
import org.example.ray.infrastructure.coder.RpcMessageDecoder;
import org.example.ray.infrastructure.coder.RpcMessageEncoder;
import org.example.ray.infrastructure.netty.server.TokenBlock.DefaultTrafficBlockHandler;
import org.example.ray.util.LogUtil;
import org.example.ray.infrastructure.util.ThreadPoolFactoryUtil;
import org.example.ray.util.PropertiesFileUtil;

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
import org.springframework.stereotype.Component;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description: netty server listening the client side and run the rpc service
 */
@Component
public class NettyServer {

    public NettyServer() {

    }

    public void start() {
        LogUtil.info("netty server init");

        // first clear the registry
        ServerShutdownHook.getInstance().registerShutdownHook();

        //init listenerGroup,listen to request and relate to workerGroup
       EventLoopGroup listenerGroup = new NioEventLoopGroup(1);
        //init workerGroup,handle the request:I/0
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        //group used to for handel the request and response
        DefaultEventExecutorGroup businessGroup = new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("netty-server-business-group", false)
        );;
        LogUtil.info("netty server start");
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
                                    pipeline.addLast(new DefaultTrafficBlockHandler());
                                    pipeline.addLast(businessGroup, new NettyRpcServerHandler());
                                }
                            });
            LogUtil.info("netty server start successfully");
            LogUtil.info("netty server bind port");
            String host = InetAddress.getLocalHost().getHostAddress();
            // bind port
            ChannelFuture f = serverBootstrap.bind(host, PropertiesFileUtil.readPortFromProperties()).sync();
            // close

            f.channel().closeFuture().sync();
            LogUtil.info("netty server close");
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
