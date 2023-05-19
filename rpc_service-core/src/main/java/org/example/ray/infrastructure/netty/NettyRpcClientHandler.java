package org.example.ray.infrastructure.netty;

import java.net.InetSocketAddress;

import org.example.ray.constants.RpcConstants;
import org.example.ray.domain.RpcData;
import org.example.ray.domain.RpcRequest;
import org.example.ray.domain.RpcResponse;
import org.example.ray.domain.enums.CompressTypeEnum;
import org.example.ray.domain.enums.SerializationTypeEnum;
import org.example.ray.infrastructure.adapter.impl.RpcSendingServiceAdapterImpl;
import org.example.ray.infrastructure.factory.SingletonFactory;
import org.example.ray.infrastructure.netty.client.WaitingProcess;
import org.example.ray.infrastructure.util.LogUtil;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */

public class NettyRpcClientHandler extends SimpleChannelInboundHandler<RpcData> {

    private final RpcSendingServiceAdapterImpl adapter;

    private final WaitingProcess               waitingProcess;

    public NettyRpcClientHandler() {
        this.adapter = SingletonFactory.getInstance(RpcSendingServiceAdapterImpl.class);
        this.waitingProcess = SingletonFactory.getInstance(WaitingProcess.class);
    }

    /**
     * heart beat handle
     * 
     * @param ctx
     * @param evt
     * @throws Exception
     */
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // if the channel is free，close it
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent)evt).state();
            if (state == IdleState.WRITER_IDLE) {
                LogUtil.info("write idle happen [{}]", ctx.channel().remoteAddress());
                Channel channel = adapter.getChannel((InetSocketAddress)ctx.channel().remoteAddress());
                RpcData rpcData = new RpcData();
                rpcData.setSerializeMethodCodec(SerializationTypeEnum.HESSIAN.getCode());
                rpcData.setCompressType(CompressTypeEnum.GZIP.getCode());
                rpcData.setMessageType(RpcConstants.HEARTBEAT_REQUEST_TYPE);
                rpcData.setData(RpcConstants.PING);
                channel.writeAndFlush(rpcData).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    /**
     * Called when an exception occurs in processing a client message
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        LogUtil.error("server exceptionCaught");
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcData rpcData) throws Exception {
        LogUtil.info("Client receive message: [{}]", rpcData);
        byte messageType = rpcData.getMessageType();
        RpcData rpcMessage = new RpcData();
        setupRpcMessage(rpcMessage);

        if (rpcData.isHeartBeatResponse()) {
            LogUtil.info("heart [{}]", rpcMessage.getData());
        } else if (rpcData.isResponse()) {
            RpcResponse<Object> rpcResponse = (RpcResponse<Object>)rpcData.getData();
            waitingProcess.complete(rpcResponse);
        }
    }

    private void setupRpcMessage(RpcData rpcMessage) {
        rpcMessage.setSerializeMethodCodec(SerializationTypeEnum.HESSIAN.getCode());
        rpcMessage.setCompressType(CompressTypeEnum.GZIP.getCode());
    }

    private void
        buildAndSetRpcResponse(ChannelHandlerContext ctx, RpcRequest rpcRequest, RpcData rpcMessage, Object result) {
        if (canBuildResponse(ctx)) {
            // 如果通道是活跃且可写，则构建成功的RPC响应
            RpcResponse<Object> rpcResponse = RpcResponse.success(result, rpcRequest.getTraceId());
            rpcMessage.setData(rpcResponse);
        } else {
            // 如果通道不可写，则构建失败的RPC响应
            RpcResponse<Object> rpcResponse = RpcResponse.fail();
            rpcMessage.setData(rpcResponse);
            LogUtil.error("Not writable now, message dropped,message:{}", rpcRequest);
        }
    }

    private boolean canBuildResponse(ChannelHandlerContext ctx) {
        return ctx.channel().isActive() && ctx.channel().isWritable();
    }
}
