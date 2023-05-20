package org.example.ray.infrastructure.coder;

import org.example.ray.constants.RpcConstants;
import org.example.ray.domain.RpcData;
import org.example.ray.domain.RpcRequest;
import org.example.ray.domain.RpcResponse;
import org.example.ray.domain.enums.CompressTypeEnum;
import org.example.ray.domain.enums.SerializationTypeEnum;
import org.example.ray.infrastructure.compress.CompressService;
import org.example.ray.infrastructure.serialize.SerializationService;
import org.example.ray.infrastructure.spi.ExtensionLoader;
import org.example.ray.util.LogUtil;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 *
 *   0     1     2     3     4        5     6     7     8         9          10      11     12  13  14   15 16
 *   +-----+-----+-----+-----+--------+----+----+----+------+-----------+-------+----- --+-----+-----+-------+
 *   |   magic   code        |version | full length         | messageType| codec|compress|    RequestId       |
 *   +-----------------------+--------+---------------------+-----------+-----------+-----------+------------+
 *   |                                                                                                       |
 *   |                                         body                                                          |
 *   |                                                                                                       |
 *   |                                        ... ...                                                        |
 *   +-------------------------------------------------------------------------------------------------------+
 */

public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {

    public RpcMessageDecoder() {
        // lengthFieldOffset: magic code is 4B, and version is 1B, and then full
        // length. so value is 5
        // lengthFieldLength: full length is 4B. so value is 4
        // lengthAdjustment: full length include all data and read 9 bytes
        // before, so the left length is (fullLength-9). so values is -9
        // initialBytesToStrip: we will check magic code and version manually,
        // so do not strip any bytes. so values is 0
        this(8 * 1024 * 1024, 5, 4, -9, 0);
    }

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, int lengthAdjustment,
        int initialBytesToStrip) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, lengthAdjustment, initialBytesToStrip);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // get the bytebuf which contains the frame
        Object decode = super.decode(ctx, in);
        if (decode instanceof ByteBuf) {
            ByteBuf byteBuf = (ByteBuf)decode;
            // if data not empty, decode it
            if (byteBuf.readableBytes() >= RpcConstants.HEAD_LENGTH) {
                try {
                    return decode(byteBuf);
                } catch (Exception e) {
                    LogUtil.error("Decode error:{} ,input:{}", e, byteBuf);
                } finally {
                    byteBuf.release();
                }
            }
        }
        return decode;
    }

    /**
     * read byte array from byteBuf
     * 
     * @param byteBuf
     * @return
     */
    private Object decode(ByteBuf byteBuf) {
        LogUtil.info("start decode");
        checkMagicCode(byteBuf);
        checkVersion(byteBuf);

        int fullLength = byteBuf.readInt();
        RpcData rpcMessage = decodeRpcMessage(byteBuf);

        if (rpcMessage.isHeatBeatRequest()) {
            return handleHeatBeatRequest(rpcMessage);
        }

        if (rpcMessage.isHeartBeatResponse()) {
            return handleHeartBeatResponse(rpcMessage);
        }

        return handleNormalRequest(rpcMessage, byteBuf, fullLength);
    }

    private RpcData decodeRpcMessage(ByteBuf byteBuf) {
        LogUtil.info("start decode RpcMessage data");
        byte messageType = byteBuf.readByte();
        byte codec = byteBuf.readByte();
        byte compress = byteBuf.readByte();
        int traceId = byteBuf.readInt();

        return RpcData.builder()
                .serializeMethodCodec(codec)
                .traceId(traceId)
                .compressType(compress)
                .messageType(messageType)
                .build();
    }

    private RpcData handleHeatBeatRequest(RpcData rpcMessage) {
        rpcMessage.setData(RpcConstants.PING);
        return rpcMessage;
    }

    private RpcData handleHeartBeatResponse(RpcData rpcMessage) {
        rpcMessage.setData(RpcConstants.PONG);
        return rpcMessage;
    }

    private Object handleNormalRequest(RpcData rpcMessage, ByteBuf byteBuf, int fullLength) {
        int bodyLength = fullLength - RpcConstants.HEAD_LENGTH;
        if (bodyLength <= 0) {
            return rpcMessage;
        }
        return decodeBody(rpcMessage, byteBuf, bodyLength);
    }

    private RpcData decodeBody(RpcData rpcMessage, ByteBuf byteBuf, Integer bodyLength) {
        LogUtil.info("start decode body");
        byte[] bodyBytes = new byte[bodyLength];
        byteBuf.readBytes(bodyBytes);
        // decompose
        String compressName = CompressTypeEnum.getName(rpcMessage.getCompressType());
        CompressService extension =
            ExtensionLoader.getExtensionLoader(CompressService.class).getExtension(compressName);
        bodyBytes = extension.decompress(bodyBytes);
        // deserialize
        if (rpcMessage.getMessageType() == RpcConstants.REQUEST_TYPE) {
            RpcRequest rpcRequest = ExtensionLoader.getExtensionLoader(SerializationService.class)
                .getExtension(SerializationTypeEnum.getName(rpcMessage.getSerializeMethodCodec()))
                .deserialize(bodyBytes, RpcRequest.class);
            rpcMessage.setData(rpcRequest);
        } else {
            RpcResponse rpcResponse = ExtensionLoader.getExtensionLoader(SerializationService.class)
                .getExtension(SerializationTypeEnum.getName(rpcMessage.getSerializeMethodCodec()))
                .deserialize(bodyBytes, RpcResponse.class);
            rpcMessage.setData(rpcResponse);
        }
        return rpcMessage;

    }

    private void checkVersion(ByteBuf byteBuf) {
        byte version = byteBuf.readByte();
        if (version != RpcConstants.VERSION) {
            throw new IllegalArgumentException("version is not compatible: " + version);
        }
    }

    private void checkMagicCode(ByteBuf byteBuf) {
        int length = RpcConstants.MAGIC_NUMBER.length;
        byte[] magicNumber = new byte[length];
        byteBuf.readBytes(magicNumber);
        for (int i = 0; i < length; i++) {
            if (magicNumber[i] != RpcConstants.MAGIC_NUMBER[i]) {
                throw new IllegalArgumentException("Unknown magic code: " + new String(magicNumber));
            }
        }
    }
}
