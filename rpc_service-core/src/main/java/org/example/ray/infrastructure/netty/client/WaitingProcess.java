package org.example.ray.infrastructure.netty.client;

import org.example.ray.domain.RpcResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
public class WaitingProcess {
    public WaitingProcess() {
    }

    private static final Map<String, CompletableFuture<RpcResponse<Object>>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String traceId, CompletableFuture<RpcResponse<Object>> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(traceId, future);
    }

}
