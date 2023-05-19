package org.example.ray.infrastructure.loadbalance;

import lombok.extern.slf4j.Slf4j;
import org.example.ray.domain.RpcRequest;
import org.example.ray.enums.LoadBalanceType;
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
