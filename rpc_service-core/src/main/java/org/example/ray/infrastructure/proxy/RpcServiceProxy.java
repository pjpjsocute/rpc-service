package org.example.ray.infrastructure.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import org.example.ray.domain.RpcRequest;
import org.example.ray.domain.RpcResponse;
import org.example.ray.domain.RpcServiceConfig;
import org.example.ray.infrastructure.adapter.RpcSendingServiceAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
@Slf4j
public class RpcServiceProxy implements InvocationHandler {

    private final RpcSendingServiceAdapter sendingServiceAdapter;

    private final RpcServiceConfig         config;

    public RpcServiceProxy(RpcSendingServiceAdapter sendingServiceAdapter, RpcServiceConfig config) {
        this.sendingServiceAdapter = sendingServiceAdapter;
        this.config = config;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        log.info("invoked method: [{}]", method.getName());
        RpcRequest rpcRequest = RpcRequest.builder()
            .methodName(method.getName())
            .parameters(args)
            .interfaceName(method.getDeclaringClass().getName())
            .paramTypes(method.getParameterTypes())
            .traceId(UUID.randomUUID().toString())
            .project(config.getProject())
            .version(config.getVersion())
            .build();

        RpcResponse<Object> rpcResponse = null;
        Object o = sendingServiceAdapter.sendRpcRequest(rpcRequest);

        CompletableFuture<RpcResponse<Object>> completableFuture =
            (CompletableFuture<RpcResponse<Object>>)sendingServiceAdapter.sendRpcRequest(rpcRequest);
        try {
            rpcResponse = completableFuture.get();
            return rpcResponse.getData();
        } catch (Exception e) {
            log.error("occur exception:", e);
        }
        return null;
    }

    /**
     * get the proxy object
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Class<T> clazz) {
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[] {clazz}, this);
    }
}
