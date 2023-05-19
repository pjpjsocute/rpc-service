package org.example.ray.infrastructure.adapter.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.example.ray.constants.RpcConstants;
import org.example.ray.infrastructure.factory.SingletonFactory;
import org.example.ray.domain.RpcData;
import org.example.ray.domain.RpcRequest;
import org.example.ray.domain.RpcResponse;
import org.example.ray.domain.enums.CompressTypeEnum;
import org.example.ray.domain.enums.SerializationTypeEnum;
import org.example.ray.infrastructure.adapter.RpcSendingServiceAdapter;
import org.example.ray.infrastructure.adapter.RpcServiceFindingAdapter;
import org.example.ray.infrastructure.netty.NettyRpcClientHandler;
import org.example.ray.infrastructure.netty.RpcMessageDecoder;
import org.example.ray.infrastructure.netty.RpcMessageEncoder;
import org.example.ray.infrastructure.netty.client.AddressChannelManager;
import org.example.ray.infrastructure.netty.client.WaitingProcess;
import org.example.ray.infrastructure.util.LogUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */


public class RpcSendingServiceAdapterImpl implements RpcSendingServiceAdapter {

    /**
     * EventLoopGroup is a multithreaded event loop that handles I/O operation.
     */
    private final EventLoopGroup           eventLoopGroup;

    /**
     * Bootstrap helt setting and start netty client
     */
    private final Bootstrap                bootstrap;

    private final RpcServiceFindingAdapter findingAdapter;

    private final AddressChannelManager    addressChannelManager;


    public RpcSendingServiceAdapterImpl() {
        this.findingAdapter = SingletonFactory.getInstance(RpcServiceFindingAdapter.class);
        this.addressChannelManager = SingletonFactory.getInstance(AddressChannelManager.class);
        // initialize
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
            .channel(NioSocketChannel.class)
            .handler(new LoggingHandler(LogLevel.INFO))
            // The timeout period for the connection.
            // If this time is exceeded or if the connection cannot be
            // established, the connection fails.
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ChannelPipeline p = ch.pipeline();
                    // If no data is sent to the server within 15 seconds, a
                    // heartbeat request is sent
                    p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                    p.addLast(new RpcMessageEncoder());
                    p.addLast(new RpcMessageDecoder());
                    p.addLast(new NettyRpcClientHandler());
                }
            });
    }

    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        // init return value
        CompletableFuture<RpcResponse<Object>> result = new CompletableFuture<>();
        // get server address
        //
        InetSocketAddress address = findingAdapter.findServiceAddress(rpcRequest);
        // get a channel which mapper to a address
        Channel channel = fetchAndConnectChannel(address);
        if (channel.isActive()) {
            // add to a waitList
            SingletonFactory.getInstance(WaitingProcess.class).put(rpcRequest.getTraceId(), result);

            // can choose compress method,code method
            RpcData rpcData = RpcData.builder()
                .data(rpcRequest)
                .serializeMethodCodec(SerializationTypeEnum.HESSIAN.getCode())
                .compressType(CompressTypeEnum.GZIP.getCode())
                .messageType(RpcConstants.REQUEST_TYPE)
                .build();

            channel.writeAndFlush(rpcData).addListener((ChannelFutureListener)future -> {
                if (future.isSuccess()) {
                    LogUtil.info("client send message: [{}]", rpcData);
                } else {
                    future.channel().close();
                    result.completeExceptionally(future.cause());
                    LogUtil.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return result;
    }

    private Channel fetchAndConnectChannel(InetSocketAddress address) {
        Channel channel = addressChannelManager.get(address);
        if (channel == null) {
            // connect to service to get new address and rebuild the channel
            channel = connect(address);
            addressChannelManager.set(address, channel);
        }
        return channel;
    }

    private Channel connect(InetSocketAddress address) {
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(address).addListener((ChannelFutureListener)future -> {
            if (future.isSuccess()) {
                // set channel to future
                LogUtil.info("The client has connected [{}] successful!", address.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        Channel channel = null;
        try {
            channel = completableFuture.get();
        } catch (Exception e) {
            LogUtil.error("occur exception when connect to server:", e);
        }
        return channel;
    }

    public Channel getChannel(InetSocketAddress inetSocketAddress) {
        Channel channel = addressChannelManager.get(inetSocketAddress);
        if (channel == null) {
            channel =  connect(inetSocketAddress);
            addressChannelManager.set(inetSocketAddress, channel);
        }
        return channel;
    }
}
