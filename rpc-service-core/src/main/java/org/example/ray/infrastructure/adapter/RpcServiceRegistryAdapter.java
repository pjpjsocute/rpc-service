package org.example.ray.infrastructure.adapter;

import org.example.ray.domain.RpcServiceConfig;
import org.example.ray.infrastructure.spi.SPI;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description: Registration service for registration into zk
 * todo: wait for the implementation of the registration center
 */
@SPI
public interface RpcServiceRegistryAdapter {

    /**
     * @param rpcServiceConfig rpc service related attributes
     */
    void registryService(RpcServiceConfig rpcServiceConfig);

    /**
     * @param rpcClassName rpc class name
     * @return service object
     */
    Object getService(String rpcClassName);



}
