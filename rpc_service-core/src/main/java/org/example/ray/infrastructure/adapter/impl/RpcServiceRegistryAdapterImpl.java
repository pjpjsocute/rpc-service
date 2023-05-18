package org.example.ray.infrastructure.adapter.impl;

import static org.example.ray.constants.RpcConstants.NETTY_SERVER_PORT;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.curator.framework.CuratorFramework;
import org.example.ray.provider.domain.RpcServiceConfig;
import org.example.ray.infrastructure.adapter.RpcServiceRegistryAdapter;
import org.example.ray.infrastructure.util.LogUtil;
import org.example.ray.infrastructure.zk.util.CuratorUtils;
import org.springframework.stereotype.Component;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description: Registration service for registration into zk todo: wait for
 *               the implementation of the registration center
 */
@Component

public class RpcServiceRegistryAdapterImpl implements RpcServiceRegistryAdapter {

    private final Map<String, Object> serviceMap = new ConcurrentHashMap<>();

    @Override
    public void registryService(RpcServiceConfig rpcServiceConfig) {
        // first get address and service
        try {
            String hostAddress = InetAddress.getLocalHost().getHostAddress();
            // add service to map cache
            registerServiceToMap(rpcServiceConfig);
            // add service to zk
            registerServiceToZk(rpcServiceConfig.getRpcServiceName(),
                new InetSocketAddress(hostAddress, NETTY_SERVER_PORT));
        } catch (UnknownHostException e) {
            LogUtil.error("occur exception when getHostAddress", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public Object getService(String rpcClassName) {
        return null;
    }

    @Override
    public List<Object> getServices(String rpcServiceName) {
        return null;
    }

    private void registerServiceToZk(String rpcServiceName, InetSocketAddress inetSocketAddress) {
        String servicePath = CuratorUtils.ZK_REGISTER_ROOT_PATH + "/" + rpcServiceName + inetSocketAddress.toString();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        CuratorUtils.createPersistentNode(zkClient, servicePath);
    }

    private void registerServiceToMap(RpcServiceConfig rpcServiceConfig) {
        String rpcServiceName = rpcServiceConfig.getRpcServiceName();
        if (serviceMap.containsKey(rpcServiceName)) {
            return;
        }
        serviceMap.put(rpcServiceName, rpcServiceConfig.getService());
    }
}
