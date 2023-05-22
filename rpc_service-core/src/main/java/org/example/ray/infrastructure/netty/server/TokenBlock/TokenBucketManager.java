package org.example.ray.infrastructure.netty.server.TokenBlock;

import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.example.ray.util.LogUtil;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * @author zhoulei
 * @create 2023/5/22
 * @description: token bucket manager
 */
public class TokenBucketManager {

    private static final ConcurrentHashMap<String, Semaphore> tokenBuckets = new ConcurrentHashMap<>();;

    private static final ConcurrentHashMap<String, Boolean>   refillThreads = new ConcurrentHashMap<>();

    private final ThreadFactory                        namedThreadFactory;

    private final ExecutorService                      refillThreadPool;

    private static final ConcurrentHashMap<String, LinkedBlockingQueue<DefaultMessage>>  queueMap = new ConcurrentHashMap<>();

    public TokenBucketManager() {
        namedThreadFactory = new ThreadFactoryBuilder().setNameFormat("token-refresh-thread-pool-%d").build();
        refillThreadPool =
            new ThreadPoolExecutor(31, 50, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue(10), namedThreadFactory);
    }

    public Semaphore getOrCreateTokenBucket(String ip, String serviceName, int limit) {
        return tokenBuckets.computeIfAbsent(serviceName, k -> new Semaphore(limit));
    }

    public Semaphore updateTokenBucket(String ip, String serviceName, int limit) {
        return tokenBuckets.put(serviceName, new Semaphore(limit));
    }

    public void release(String ip, String serviceName, int limit) {
        tokenBuckets.get(serviceName).release(limit);
    }

    public List<Semaphore> getAllTokenBuckets() {
        return (List<Semaphore>)tokenBuckets.values();
    }

    public void startRefillThread(String ip, String serviceName, int refillRate) {
        String key = serviceName;
        if (refillThreads.putIfAbsent(key, true) == null) {
            Semaphore tokens = getOrCreateTokenBucket(ip, serviceName, refillRate);
            LogUtil.info("Starting refresh thread for service:{},ip:{}", serviceName, ip);

            refillThreadPool.execute(() -> {
                Thread.currentThread().setName("RefillThread-" + key);
                try {
                    while (true) {
                        tokens.release(refillRate);
                        TimeUnit.MILLISECONDS.sleep(1);
                    }
                } catch (InterruptedException ignored) {
                }
            });
        }
    }

    public LinkedBlockingQueue<DefaultMessage>  addOrCreate(String serviceName,DefaultMessage message){
        return queueMap.computeIfAbsent(serviceName, k -> {
            LinkedBlockingQueue<DefaultMessage> queue = new LinkedBlockingQueue<>();
            queue.offer(message);
            return queue;
        });
    }

}
