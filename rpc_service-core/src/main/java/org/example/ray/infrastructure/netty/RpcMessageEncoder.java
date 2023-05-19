package org.example.ray.infrastructure.netty;

import org.example.ray.constants.RpcConstants;
import org.example.ray.domain.RpcData;
import org.example.ray.domain.enums.CompressTypeEnum;
import org.example.ray.domain.enums.RpcErrorMessageEnum;
import org.example.ray.domain.enums.SerializationTypeEnum;
import org.example.ray.expection.RpcException;
import org.example.ray.infrastructure.compress.CompressService;
import org.example.ray.infrastructure.serialize.SerializationService;
import org.example.ray.infrastructure.spi.ExtensionLoader;
import org.example.ray.infrastructure.util.LogUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description: a codec structure is as follows: 0 1 2 3 4 5 6 7 8 9 10 11 12
 *               13 14 15 16
 *               +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+-----
 *               --+-----+-----+-------+ | magic code |version | full length |
 *               messageType| codec|compress| traceId |
 *               +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *               | | | body | | | | ... ... |
 *               +-------------------------------------------------------------------------------------------------------+
 *               4B magic code（魔法数） 1B version（版本） 4B full length（消息长度） 1B
 *               messageType（消息类型） 1B compress（压缩类型） 1B codec（序列化类型） 4B
 *               requestId（请求的Id）
 */

public class RpcMessageEncoder extends MessageToByteEncoder<RpcData> {

    private static final AtomicInteger ATOMIC_INTEGER = new AtomicInteger(0);

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, RpcData rpcData, ByteBuf byteBuf) {
        try {
            // write magic code and version 0-5
            byteBuf.writeBytes(RpcConstants.MAGIC_NUMBER);
            byteBuf.writeByte(RpcConstants.VERSION);

            // marked full length index.
            int fullLengthIndex = byteBuf.writerIndex();

            // write placeholder for full length 9+
            byteBuf.writerIndex(byteBuf.writerIndex() + 4);

            // write message type
            byteBuf.writeByte(rpcData.getMessageType());
            // write codec
            byteBuf.writeByte(rpcData.getSerializeMethodCodec());
            // write compress
            byteBuf.writeByte(rpcData.getCompressType());
            // write requestId
            byteBuf.writeInt(ATOMIC_INTEGER.getAndIncrement());

            byte[] bodyBytes = null;
            int fullLength = RpcConstants.HEAD_LENGTH;
            // can send request
            if (rpcData.canSendRequest()) {
                LogUtil.info("serialize request start");
                bodyBytes = ExtensionLoader.getExtensionLoader(SerializationService.class)
                    .getExtension(SerializationTypeEnum.getName(rpcData.getSerializeMethodCodec()))
                    .serialize(rpcData.getData());
                LogUtil.info("serialize request end");

                String compressName = CompressTypeEnum.getName(rpcData.getCompressType());
                CompressService extension =
                    ExtensionLoader.getExtensionLoader(CompressService.class).getExtension(compressName);
                bodyBytes = extension.compress(bodyBytes);
                fullLength += bodyBytes.length;
            }

            if (bodyBytes != null) {
                byteBuf.writeBytes(bodyBytes);
            }
            int writeIndex = byteBuf.writerIndex();
            byteBuf.writerIndex(fullLengthIndex);
            byteBuf.writeInt(fullLength);
            byteBuf.writerIndex(writeIndex);
        } catch (Exception e) {
            LogUtil.error("Encode request error:{},data:{}", e, rpcData);
            throw new RpcException(RpcErrorMessageEnum.REQUEST_ENCODE_FAIL.getCode(),
                RpcErrorMessageEnum.REQUEST_ENCODE_FAIL.getMessage());
        }

    }
}
