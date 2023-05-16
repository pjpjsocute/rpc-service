package org.example.ray.infrastructure.adapter;

import org.example.ray.domain.RpcRequest;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
public interface RpcSendingServiceAdapter {
    /**
     * send rpc request to server and get result
     *
     * @param rpcRequest message body
     * @return data from server
     */
    Object sendRpcRequest(RpcRequest rpcRequest);
}
