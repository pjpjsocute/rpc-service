package org.example.ray.infrastructure.adapter;

import org.example.ray.domain.RpcRequest;
import org.example.ray.infrastructure.spi.SPI;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
@SPI
public interface RpcSendingServiceAdapter {
    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
