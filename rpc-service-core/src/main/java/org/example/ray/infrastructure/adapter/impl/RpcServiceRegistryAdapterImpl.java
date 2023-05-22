package org.example.ray.infrastructure.adapter.impl;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.example.ray.domain.RpcServiceConfig;
import org.example.ray.domain.enums.RpcErrorMessageEnum;
import org.example.ray.expection.RpcException;
import org.example.ray.infrastructure.adapter.RpcServiceRegistryAdapter;
import org.example.ray.infrastructure.zk.CuratorClient;
import org.example.ray.util.LogUtil;
import org.example.ray.util.PropertiesFileUtil;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description: Registration service for registration into zk
 *               the implementation of the registration center
 */

public class RpcServiceRegistryAdapterImpl implements RpcServiceRegistryAdapter {

    /**
     * cache map
     */
    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    @Override
    public void registryService(RpcServiceConfig rpcServiceConfig) {
        try {
            // first get address and service
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            // add service to zk
            LogUtil.info("add service to zk,service name{},host:{}", rpcServiceConfig.fetchRpcServiceName(),hostAddress);
            registerServiceToZk(rpcServiceConfig.fetchRpcServiceName(),
                new InetSocketAddress(hostAddress, PropertiesFileUtil.readPortFromProperties()));
            // add service to map cache
            registerServiceToMap(rpcServiceConfig);
        } catch (UnknownHostException e) {
            LogUtil.error("occur exception when getHostAddress", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object getService(String rpcServiceName) {
        Object service = serviceMap.get(rpcServiceName);
        if (null == service) {
            throw new RpcException(RpcErrorMessageEnum.SERVICE_CAN_NOT_BE_FOUND.getCode(),"service not found");
        }
        return service;
    }


    private void registerServiceToZk(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorClient.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorClient.getZkClient();
        CuratorClient.createPersistentNode(zkClient, servicePath);
    }

    private void registerServiceToMap(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.fetchRpcServiceName();
        if (serviceMap.containsKey(rpcServiceName)) {
            return;
        }
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
    }
}
