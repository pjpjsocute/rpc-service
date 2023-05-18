package org.example.ray.infrastructure.netty;

import javax.annotation.Resource;

import io.netty.channel.ChannelHandler;
import org.example.ray.constants.RpcConstants;
import org.example.ray.provider.domain.RpcData;
import org.example.ray.provider.domain.RpcRequest;
import org.example.ray.provider.domain.RpcResponse;
import org.example.ray.provider.domain.enums.CompressTypeEnum;
import org.example.ray.provider.domain.enums.SerializationTypeEnum;
import org.example.ray.infrastructure.util.LogUtil;
import org.springframework.stereotype.Component;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */

@Component
public class NettyRpcServerHandler extends SimpleChannelInboundHandler<RpcData> {
    /**
     * Read the message transmitted by the server
     */
    @Resource
    private RpcRequestHandler rpcRequestHandler;

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
            LogUtil.info("IdleStateEvent happen, so close the connection");
            ctx.channel().close();
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
        LogUtil.info("Server receive message: [{}]", rpcData);
        RpcData rpcMessage = new RpcData();
        setupRpcMessage(rpcMessage);

        if (rpcData.isHeatBeatRequest()) {
            handleHeartbeat(rpcMessage);
        } else {
            handleRpcRequest(ctx, rpcData, rpcMessage);
        }
    }

    private void setupRpcMessage(RpcData rpcMessage) {
        rpcMessage.setSerializeMethodCodec(SerializationTypeEnum.HESSIAN.getCode());
        rpcMessage.setCompressType(CompressTypeEnum.GZIP.getCode());
    }

    private void handleHeartbeat(RpcData rpcMessage) {
        rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
        rpcMessage.setData(RpcConstants.PONG);
    }

    private void handleRpcRequest(ChannelHandlerContext ctx, RpcData rpcData, RpcData rpcMessage) throws Exception {
        RpcRequest rpcRequest = (RpcRequest)rpcData.getData();

        // invoke target method
        Object result = rpcRequestHandler.handle(rpcRequest);
        LogUtil.info("Server get result: {}", result);

        rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);
        buildAndSetRpcResponse(ctx, rpcRequest, rpcMessage, result);
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
