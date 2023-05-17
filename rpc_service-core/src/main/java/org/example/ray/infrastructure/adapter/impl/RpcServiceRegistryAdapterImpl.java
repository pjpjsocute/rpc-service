package org.example.ray.infrastructure.adapter.impl;

import java.util.List;

import org.example.ray.domain.RpcRequest;
import org.example.ray.domain.RpcServiceConfig;
import org.example.ray.infrastructure.adapter.RpcSendingServiceAdapter;
import org.example.ray.infrastructure.adapter.RpcServiceRegistryAdapter;
import org.springframework.stereotype.Component;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description: Registration service for registration into zk
 * todo: wait for the implementation of the registration center
 */
@Component
public class RpcServiceRegistryAdapterImpl implements RpcServiceRegistryAdapter {


    @Override
    public void registryService(RpcServiceConfig rpcServiceConfig) {

    }

    @Override
    public Object getService(String rpcClassName) {
        return null;
    }

    @Override
    public List<Object> getServices(String rpcServiceName) {
        return null;
    }

    @Override
    public void releaseService(RpcServiceConfig rpcServiceConfig) {

    }
}
