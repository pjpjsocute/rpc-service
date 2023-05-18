package org.example.ray.infrastructure.netty;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import io.netty.channel.ChannelHandler;
import org.example.ray.provider.domain.RpcRequest;
import org.example.ray.provider.domain.enums.RpcErrorMessageEnum;
import org.example.ray.expection.RpcException;
import org.example.ray.infrastructure.adapter.RpcServiceRegistryAdapter;
import org.example.ray.infrastructure.util.LogUtil;
import org.springframework.stereotype.Component;

/**
 * RpcRequest processor
 *
 * @author shuang.kou
 * @createTime 2020年05月13日 09:05:00
 */

@Component

public class RpcRequestHandler {

    private final RpcServiceRegistryAdapter adapter;

    public RpcRequestHandler(RpcServiceRegistryAdapter adapter) {
        this.adapter = adapter;
    }

    /**
     * Processing rpcRequest: call the corresponding method, and then return the
     * method
     */
    public Object handle(RpcRequest request) {
        Object service = adapter.getService(request.getServiceName());
        return invoke(request, service);
    }

    /**
     * get method execution results
     *
     * @param rpcRequest client request
     * @param service service object
     * @return the result of the target method execution
     */
    private Object invoke(RpcRequest rpcRequest, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(rpcRequest.getMethodName(), rpcRequest.getParamTypes());
            result = method.invoke(service, rpcRequest.getParameters());
            LogUtil.info("service:[{}] successful invoke method:[{}]", rpcRequest.getServiceName(),
                rpcRequest.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException
            | IllegalAccessException e) {
            LogUtil.error("occur exception when invoke target method,error:{},RpcRequest:{}", e, rpcRequest);
            throw new RpcException(RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE.getCode(), RpcErrorMessageEnum.SERVICE_INVOCATION_FAILURE.getMessage());
        }
        return result;
    }
}
