package org.example.ray.infrastructure.netty.server.TokenBlock;

import java.net.InetSocketAddress;
import java.util.concurrent.Semaphore;

import org.example.ray.domain.RpcData;
import org.example.ray.infrastructure.factory.SingletonFactory;
import org.example.ray.util.LogUtil;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhoulei
 * @create 2023/5/22
 * @description: a simple traffic block handler, it will block the request when the token is not enough
 * based on the token bucket algorithm
 */
public class DefaultTrafficBlockHandler extends ChannelDuplexHandler {

    /**
     * token
     */
    private final TokenBucketManager manager;

    /**
     * flow speed
     */
    private final int                refillRate;

    public DefaultTrafficBlockHandler() {
        int capacity = 10;
        this.manager = SingletonFactory.getInstance(TokenBucketManager.class);
        this.refillRate = capacity / 10; //

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcData) {
            RpcData rpcData = (RpcData)msg;
            if (rpcData.isHeatBeatRequest()) {
                super.channelRead(ctx, msg);
                return;
            }
            // 获取或创建对应的令牌桶
            Semaphore tokens = manager.getOrCreateTokenBucket("default", "default", refillRate);
            // 开启填充线程
            manager.startRefillThread("default", "default", refillRate);

            if (!tokens.tryAcquire()) {
                // 令牌不足，将请求添加到队列中
                LogUtil.info("blocking request, ip:{}, serviceName:{}",
                    ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress());
                ctx.close();
            } else {
                super.channelRead(ctx, msg);
            }
        }
    }

}
