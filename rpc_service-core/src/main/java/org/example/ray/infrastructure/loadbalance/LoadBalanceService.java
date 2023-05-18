package org.example.ray.infrastructure.loadbalance;

import org.example.ray.domain.RpcRequest;
import org.example.ray.enums.LoadBalanceType;

import java.util.List;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
public interface LoadBalanceService{

    LoadBalanceType fetchLoadBalanceType();

    /**
     * Choose one from the list of existing service addresses list
     *
     * @param serviceUrlList Service address list
     * @param rpcRequest
     * @return target service address
     */
    String selectServiceAddress(List<String> serviceUrlList, RpcRequest rpcRequest);
}
