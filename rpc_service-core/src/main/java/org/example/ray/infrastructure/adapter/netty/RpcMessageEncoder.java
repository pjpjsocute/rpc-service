package org.example.ray.infrastructure.adapter.netty;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.example.ray.domain.RpcData;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
public class RpcMessageEncoder extends MessageToByteEncoder<RpcData> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcData rpcData, ByteBuf byteBuf) throws Exception {

    }
}
