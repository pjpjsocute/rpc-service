package org.example.ray.infrastructure.loadbalance;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.example.ray.provider.domain.RpcRequest;
import org.example.ray.enums.LoadBalanceType;
import org.example.ray.infrastructure.util.LogUtil;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
@Component

public class LoadBalanceStrategy implements ApplicationContextAware {

    private ApplicationContext         applicationContext;

    private Map<LoadBalanceType, LoadBalanceService> serviceMap;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
        initStrategy();
    }

    private void initStrategy() {
        Map<String, LoadBalanceService> beanMap = applicationContext.getBeansOfType(LoadBalanceService.class);
        this.serviceMap = beanMap.values()
            .stream()
            .collect(Collectors.toMap(LoadBalanceService::fetchLoadBalanceType, Function.identity()));
    }


    /**
     * @param serviceUrlList service address list
     * @param rpcRequest
     * @param loadBalanceType
     * @return
     */
    public String findService(List<String> serviceUrlList, RpcRequest rpcRequest,LoadBalanceType loadBalanceType) {
        return findService(loadBalanceType).selectServiceAddress(serviceUrlList, rpcRequest);
    }


    private LoadBalanceService findService(LoadBalanceType loadBalanceType) {
        LoadBalanceService loadBalanceService = serviceMap.get(loadBalanceType);
        if (loadBalanceService == null) {
            LogUtil.error("oad balance Service is null,{}", loadBalanceType);
            throw new RuntimeException("load balanceService is null");
        }
        return loadBalanceService;
    }
}
