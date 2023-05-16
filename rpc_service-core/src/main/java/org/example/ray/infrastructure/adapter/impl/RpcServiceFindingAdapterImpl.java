package org.example.ray.infrastructure.adapter.impl;

import org.example.ray.domain.RpcRequest;
import org.example.ray.infrastructure.adapter.RpcServiceFindingAdapter;

import java.net.InetSocketAddress;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
public class RpcServiceFindingAdapterImpl implements RpcServiceFindingAdapter {
    @Override
    public InetSocketAddress findService(RpcRequest rpcRequest) {
        return null;
    }
}
