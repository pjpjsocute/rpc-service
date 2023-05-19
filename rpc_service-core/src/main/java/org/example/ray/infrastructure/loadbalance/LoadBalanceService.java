package org.example.ray.infrastructure.loadbalance;

import org.example.ray.domain.RpcRequest;
import org.example.ray.infrastructure.spi.SPI;

import java.util.List;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
@SPI
public interface LoadBalanceService{

    /**
     * Choose one from the list of existing service addresses list
     *
     * @param serviceUrlList Service address list
     * @param rpcRequest
     * @return target service address
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
