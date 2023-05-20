package org.example.ray.infrastructure.adapter;

import org.example.ray.domain.RpcRequest;
import org.example.ray.infrastructure.spi.SPI;

import java.net.InetSocketAddress;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
@SPI
public interface RpcServiceFindingAdapter {
    /**
     * lookup service by rpcServiceName
     * todo: can optimize with a cache
     * @param rpcRequest rpc service pojo
     * @return service address
     */
    InetSocketAddress findServiceAddress(RpcRequest rpcRequest);
}
