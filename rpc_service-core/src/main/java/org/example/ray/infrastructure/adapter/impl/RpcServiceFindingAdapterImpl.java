package org.example.ray.infrastructure.adapter.impl;

import static org.example.ray.constants.RpcConstants.LOAD_BALANCE;

import java.net.InetSocketAddress;
import java.util.List;

import org.apache.curator.framework.CuratorFramework;
import org.example.ray.provider.domain.RpcRequest;
import org.example.ray.enums.LoadBalanceType;
import org.example.ray.infrastructure.adapter.RpcServiceFindingAdapter;
import org.example.ray.infrastructure.loadbalance.LoadBalanceStrategy;
import org.example.ray.infrastructure.zk.util.CuratorUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
@Component
public class RpcServiceFindingAdapterImpl implements RpcServiceFindingAdapter {

    @Autowired
    private LoadBalanceStrategy loadBalanceStrategy;

    @Override
    public InetSocketAddress findServiceAddress(RpcRequest rpcRequest) {
        String serviceName = rpcRequest.fetchRpcServiceName();
        CuratorFramework zkClient = CuratorUtils.getZkClient();
        List<String> serviceAddresseList = CuratorUtils.getChildrenNodes(zkClient, serviceName);

        String service = loadBalanceStrategy.findService(serviceAddresseList, rpcRequest,
            LoadBalanceType.getLoadBalanceType(LOAD_BALANCE));
        String[] socketAddressArray = service.split(":");
        String host = socketAddressArray[0];
        int port = Integer.parseInt(socketAddressArray[1]);
        return new InetSocketAddress(host, port);
    }
}
