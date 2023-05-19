package org.example.ray.infrastructure.adapter.impl;

import static org.example.ray.constants.RpcConstants.LOAD_BALANCE;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.example.ray.domain.RpcRequest;
import org.example.ray.infrastructure.adapter.RpcServiceFindingAdapter;
import org.example.ray.infrastructure.loadbalance.LoadBalanceService;
import org.example.ray.infrastructure.spi.ExtensionLoader;
import org.example.ray.infrastructure.zk.util.CuratorUtils;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */

public class RpcServiceFindingAdapterImpl implements RpcServiceFindingAdapter {

    private final LoadBalanceService loadBalanceService;

    public RpcServiceFindingAdapterImpl() {
        this.loadBalanceService = ExtensionLoader.getExtensionLoader(LoadBalanceService.class).getExtension(LOAD_BALANCE);
    }

    @Override
    public InetSocketAddress findServiceAddress(RpcRequest rpcRequest) {
        String serviceName = rpcRequest.fetchRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceAddresseList = CuratorUtils.getChildrenNodes(zkClient, serviceName);


        String service = loadBalanceService.selectServiceAddress(serviceAddresseList, rpcRequest);
        String[] socketAddressArray = service.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
