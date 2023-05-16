package org.example.ray.infrastructure.adapter;

import org.example.ray.domain.RpcRequest;

import java.net.InetSocketAddress;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
public interface RpcServiceFindingAdapter {
    /**
     * lookup service by rpcServiceName
     *
     * @param rpcRequest rpc service pojo
     * @return service address
     */
    InetSocketAddress findService(RpcRequest rpcRequest);
}
