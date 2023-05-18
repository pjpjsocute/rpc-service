package org.example.ray.infrastructure.adapter;

import org.example.ray.provider.domain.RpcServiceConfig;

import java.util.List;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description: Registration service for registration into zk
 * todo: wait for the implementation of the registration center
 */
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
