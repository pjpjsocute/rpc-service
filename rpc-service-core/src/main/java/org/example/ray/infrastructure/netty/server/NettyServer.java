package org.example.ray.infrastructure.netty.server;

import java.net.InetAddress;
import java.net.UnknownHostException;
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

        ServerShutdownHook.getInstance().registerShutdownHook();

        EventLoopGroup listenerGroup = initListenerGroup();
        EventLoopGroup workerGroup = initWorkerGroup();
        DefaultEventExecutorGroup businessGroup = initBusinessGroup();

        LogUtil.info("netty server start");

        try {
            ServerBootstrap serverBootstrap = configureServerBootstrap(listenerGroup, workerGroup, businessGroup);
            bindAndListen(serverBootstrap);
        } catch (Exception e) {
            LogUtil.error("occur exception when start server:", e);
        } finally {
            shutdown(listenerGroup, workerGroup, businessGroup);
        }

    }

    private EventLoopGroup initListenerGroup() {
        return new NioEventLoopGroup(1);
    }

    private EventLoopGroup initWorkerGroup() {
        return new NioEventLoopGroup();
    }

    private DefaultEventExecutorGroup initBusinessGroup() {
        return new DefaultEventExecutorGroup(
                Runtime.getRuntime().availableProcessors() * 2,
                ThreadPoolFactoryUtil.createThreadFactory("netty-server-business-group", false)
        );
    }

    private ServerBootstrap configureServerBootstrap(EventLoopGroup listenerGroup, EventLoopGroup workerGroup, DefaultEventExecutorGroup businessGroup) {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(listenerGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.SO_BACKLOG, 128)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        pipeline.addLast(new RpcMessageEncoder());
                        pipeline.addLast(new RpcMessageDecoder());
                        pipeline.addLast(new DefaultTrafficBlockHandler());
                        pipeline.addLast(businessGroup, new NettyRpcServerHandler());
                    }
                });

        return serverBootstrap;
    }

    private void bindAndListen(ServerBootstrap serverBootstrap) throws UnknownHostException, InterruptedException {
        LogUtil.info("netty server bind port:{} " , PropertiesFileUtil.readPortFromProperties());
        String host = InetAddress.getLocalHost().getHostAddress();
        ChannelFuture f = serverBootstrap.bind(host, PropertiesFileUtil.readPortFromProperties()).sync();
        f.channel().closeFuture().sync();
    }

    private void shutdown(EventLoopGroup listenerGroup, EventLoopGroup workerGroup, DefaultEventExecutorGroup businessGroup) {
        listenerGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
        businessGroup.shutdownGracefully();
    }


}
