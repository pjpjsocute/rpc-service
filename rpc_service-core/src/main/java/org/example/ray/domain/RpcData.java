package org.example.ray.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.ray.constants.RpcConstants;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcData {
    /**
     * rpc message type
     */
    private byte messageType;
    /**
     * serialization type
     */
    private byte serializeMethodCodec;
    /**
     * compress type
     */
    private byte compressType;
    /**
     * request id
     */
    private int traceId;
    /**
     * request data
     */
    private Object data;

    public boolean isHeatBeatRequest() {
        return messageType == RpcConstants.HEARTBEAT_REQUEST_TYPE;
    }

    public boolean canSendRequest() {
        return messageType != RpcConstants.HEARTBEAT_REQUEST_TYPE
                && messageType != RpcConstants.HEARTBEAT_RESPONSE_TYPE;
    }

    public boolean isHeartBeatResponse() {
        return messageType == RpcConstants.HEARTBEAT_RESPONSE_TYPE;
    }

    public boolean isResponse() {
        return messageType == RpcConstants.RESPONSE_TYPE;
    }
}
