package org.example.ray.infrastructure.adapter.impl;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.example.ray.constants.RpcConstants;
import org.example.ray.domain.RpcData;
import org.example.ray.domain.RpcRequest;
import org.example.ray.domain.RpcResponse;
import org.example.ray.domain.enums.CompressTypeEnum;
import org.example.ray.domain.enums.SerializationTypeEnum;
import org.example.ray.domain.enums.ServiceDiscoveryEnum;
import org.example.ray.infrastructure.adapter.RpcSendingServiceAdapter;
import org.example.ray.infrastructure.adapter.RpcServiceFindingAdapter;
import org.example.ray.infrastructure.coder.RpcMessageDecoder;
import org.example.ray.infrastructure.coder.RpcMessageEncoder;
import org.example.ray.infrastructure.factory.SingletonFactory;
import org.example.ray.infrastructure.netty.client.AddressChannelManager;
import org.example.ray.infrastructure.netty.client.NettyRpcClientHandler;
import org.example.ray.infrastructure.netty.client.WaitingProcessRequestQueue;
import org.example.ray.infrastructure.spi.ExtensionLoader;
import org.example.ray.util.LogUtil;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
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
    private final EventLoopGroup             eventLoopGroup;

    /**
     * Bootstrap helt setting and start netty client
     */
    private final Bootstrap                  bootstrap;

    /**
     * Service discovery
     */
    private final RpcServiceFindingAdapter   findingAdapter;

    /**
     * Channel manager,mapping channel and address
     */
    private final AddressChannelManager      addressChannelManager;

    /**
     * Waiting process request queue
     */
    private final WaitingProcessRequestQueue waitingProcessRequestQueue;

    public RpcSendingServiceAdapterImpl() {
        this.findingAdapter = ExtensionLoader.getExtensionLoader(RpcServiceFindingAdapter.class)
            .getExtension(ServiceDiscoveryEnum.ZK.getName());
        this.addressChannelManager = SingletonFactory.getInstance(AddressChannelManager.class);
        this.waitingProcessRequestQueue = SingletonFactory.getInstance(WaitingProcessRequestQueue.class);
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
        CompletableFuture<RpcResponse<Object>> result = new CompletableFuture<>();
        InetSocketAddress address = findServiceAddress(rpcRequest);
        Channel channel = fetchAndConnectChannel(address);
        if (channel.isActive()) {
            addToProcessQueue(rpcRequest.getTraceId(), result);
            RpcData rpcData = prepareRpcData(rpcRequest);
            sendRpcData(channel, rpcData, result);
        } else {
            throw new IllegalStateException();
        }
        return result;
    }
    private InetSocketAddress findServiceAddress(RpcRequest rpcRequest) {
        return findingAdapter.findServiceAddress(rpcRequest);
    }

    private void addToProcessQueue(String traceId, CompletableFuture<RpcResponse<Object>> result) {
        waitingProcessRequestQueue.put(traceId, result);
    }

    private RpcData prepareRpcData(RpcRequest rpcRequest) {
        return RpcData.builder()
                .data(rpcRequest)
                .serializeMethodCodec(SerializationTypeEnum.HESSIAN.getCode())
                .compressType(CompressTypeEnum.GZIP.getCode())
                .messageType(RpcConstants.REQUEST_TYPE)
                .build();
    }
    private void sendRpcData(Channel channel, RpcData rpcData, CompletableFuture<RpcResponse<Object>> result) {
        channel.writeAndFlush(rpcData).addListener((ChannelFutureListener)future -> {
            if (future.isSuccess()) {
                LogUtil.info("client send message: [{}]", rpcData);
            } else {
                future.channel().close();
                result.completeExceptionally(future.cause());
                LogUtil.error("Send failed:", future.cause());
            }
        });
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
                LogUtil.error("The client failed to connect to the server [{}],future", address.toString(), future);
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
            channel = connect(inetSocketAddress);
            addressChannelManager.set(inetSocketAddress, channel);
        }
        return channel;
    }
}
