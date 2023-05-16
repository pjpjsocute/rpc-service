package org.example.ray.infrastructure.config;

import org.example.ray.infrastructure.adapter.RpcSendingServiceAdapter;
import org.example.ray.infrastructure.adapter.RpcServiceFindingAdapter;
import org.example.ray.infrastructure.adapter.RpcServiceRegistryAdapter;
import org.example.ray.infrastructure.adapter.impl.RpcSendingServiceAdapterImpl;
import org.example.ray.infrastructure.adapter.impl.RpcServiceFindingAdapterImpl;
import org.example.ray.infrastructure.adapter.impl.RpcServiceRegistryAdapterImpl;
import org.example.ray.infrastructure.adapter.netty.AddressChannelManager;
import org.example.ray.infrastructure.adapter.netty.WaitingProcess;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
@Configuration
public class AutoConfiguration {

    @Bean
    public WaitingProcess waitingProcess(){
        return new WaitingProcess();
    }

    @Bean
    public AddressChannelManager addressChannelManager(){
        return new AddressChannelManager();
    }

    @Bean
    public RpcServiceRegistryAdapter rpcServiceRegistryAdapter(){
        return new RpcServiceRegistryAdapterImpl();
    }

    @Bean
    public RpcServiceFindingAdapter rpcServiceFindingAdapter(){
        return new RpcServiceFindingAdapterImpl();
    }

    @Bean
    public RpcSendingServiceAdapter rpcSendingServiceAdapter(
            WaitingProcess waitingProcess,RpcServiceFindingAdapter findingAdapter,AddressChannelManager addressChannelManager
    ){
        return new RpcSendingServiceAdapterImpl(findingAdapter, waitingProcess, addressChannelManager);
    }


}
