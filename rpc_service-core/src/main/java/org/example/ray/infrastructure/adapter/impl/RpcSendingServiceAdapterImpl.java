package org.example.ray.infrastructure.adapter.impl;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.example.ray.domain.enums.CompressTypeEnum;
import org.example.ray.domain.RpcData;
import org.example.ray.domain.RpcRequest;
import org.example.ray.domain.RpcResponse;
import org.example.ray.domain.enums.SerializationTypeEnum;
import org.example.ray.infrastructure.adapter.RpcSendingServiceAdapter;
import org.example.ray.infrastructure.adapter.RpcServiceFindingAdapter;
import org.example.ray.infrastructure.adapter.netty.AddressChannelManager;
import org.example.ray.infrastructure.adapter.netty.NettyRpcClientHandler;
import org.example.ray.infrastructure.adapter.netty.RpcMessageDecoder;
import org.example.ray.infrastructure.adapter.netty.RpcMessageEncoder;
import org.example.ray.infrastructure.adapter.netty.WaitingProcess;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
@Slf4j
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

    private final WaitingProcess           waitingProcess;

    private final AddressChannelManager    addressChannelManager;

    public RpcSendingServiceAdapterImpl(RpcServiceFindingAdapter findingAdapter, WaitingProcess waitingProcess,
        AddressChannelManager addressChannelManager) {
        this.findingAdapter = findingAdapter;
        this.waitingProcess = waitingProcess;
        this.addressChannelManager = addressChannelManager;
        // initialize
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                // The timeout period for the connection.
                // If this time is exceeded or if the connection cannot be established, the connection fails.
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline p = ch.pipeline();
                        // If no data is sent to the server within 15 seconds, a heartbeat request is sent
                        p.addLast(new IdleStateHandler(0, 5, 0, TimeUnit.SECONDS));
                        p.addLast(new RpcMessageEncoder());
                        p.addLast(new RpcMessageDecoder());
                        p.addLast(new NettyRpcClientHandler());
                    }
                });
    }


    @Override
    public Object sendRpcRequest(RpcRequest rpcRequest) {
        //init return value
        CompletableFuture<RpcResponse<Object>> result = new CompletableFuture<>();
        //get server address
        //todo: can optimize with a cache
        InetSocketAddress address = findingAdapter.findService(rpcRequest);
        //get a channel which mapper to a address
        Channel channel = fetchChannel(address);
        if (channel.isActive()){
            // add to a waitList
            waitingProcess.put(rpcRequest.getTraceId(), result);

            //can choose compress method,code method
            RpcData rpcData = RpcData.builder().data(rpcRequest)
                    .codec(SerializationTypeEnum.HESSIAN.getCode())
                    .compress(CompressTypeEnum.GZIP.getCode())
                    .messageType((byte) 1).build();

            channel.writeAndFlush(rpcData).addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    log.info("client send message: [{}]", rpcData);
                } else {
                    future.channel().close();
                    result.completeExceptionally(future.cause());
                    log.error("Send failed:", future.cause());
                }
            });
        } else {
            throw new IllegalStateException();
        }
        return result;
    }

    private Channel fetchChannel(InetSocketAddress address) {
        Channel channel = addressChannelManager.get(address);
        if (channel == null) {
            //connect to service to get new address and rebuild the channel
            channel = connect(address);
            addressChannelManager.set(address, channel);
        }
        return channel;
    }

    private Channel connect(InetSocketAddress address){
        CompletableFuture<Channel> completableFuture = new CompletableFuture<>();
        bootstrap.connect(address).addListener((ChannelFutureListener) future -> {
            if (future.isSuccess()) {
                //set channel to future
                log.info("The client has connected [{}] successful!", address.toString());
                completableFuture.complete(future.channel());
            } else {
                throw new IllegalStateException();
            }
        });
        Channel channel = null;
        try {
            channel = completableFuture.get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
        return channel;
    }
}
