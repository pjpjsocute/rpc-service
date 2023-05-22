package org.example.ray.infrastructure.netty.server.TokenBlock;

import static org.example.ray.constants.RpcConstants.BLOCK_CAPACITY;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;

import org.example.ray.domain.RpcData;
import org.example.ray.infrastructure.factory.SingletonFactory;
import org.example.ray.util.LogUtil;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;

/**
 * @author zhoulei
 * @create 2023/5/22
 * @description:
 */
public class TrafficBlockLimitHandler extends ChannelDuplexHandler {

    /**
     * token
     */
    private final TokenBucketManager            manager;

    private final BlockingQueue<DefaultMessage> queue;

    public TrafficBlockLimitHandler() {
        this.manager = SingletonFactory.getInstance(TokenBucketManager.class);

        this.queue = new LinkedBlockingQueue<>(BLOCK_CAPACITY);

        new Thread(() -> {
            try {
                while (true) {
                    DefaultMessage message = queue.take();
                    if (message != null) {
                        channelRead(message.ctx, message.msg);
                    }
                }
            } catch (Exception e) {
                LogUtil.error("TrafficBlockLimitHandler handle request error", e);
            }
        }).start();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof RpcData) {
            RpcData rpcData = (RpcData)msg;
            if (rpcData.isHeatBeatRequest()) {
                super.channelRead(ctx, msg);
                return;
            }
            // 获取服务名称
            String serviceName = getServiceName(rpcData);
            String ip = ((InetSocketAddress)ctx.channel().remoteAddress()).getAddress().getHostAddress();

            // 根据IP和服务名称获取限流值
            int limit = getLimitFromConfigService(ip, serviceName);
            // 获取或创建对应的令牌桶
            Semaphore tokens = manager.getOrCreateTokenBucket(ip, serviceName, limit);
            // 开启填充线程
            manager.startRefillThread(ip, serviceName, limit);

            if (!tokens.tryAcquire()) {
                // 令牌不足，将请求添加到队列中
                if (!queue.offer(new DefaultMessage(ctx, msg))) {
                    // 队列已满，关闭连接或者其他处理
                    ctx.close();
                }
            } else {
                super.channelRead(ctx, msg);
            }
        }
    }

    /**
     * todo:待实现
     * 
     * @param ip
     * @param serviceName
     * @return
     */
    private int getLimitFromConfigService(String ip, String serviceName) {
        return 1000;
    }

    /**
     * todo:待实现
     * 
     * @param rpcData
     * @return
     */
    private String getServiceName(RpcData rpcData) {
        return "test";
    }

}
