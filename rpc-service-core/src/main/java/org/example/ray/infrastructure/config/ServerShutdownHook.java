package org.example.ray.infrastructure.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.ray.infrastructure.util.ThreadPoolFactoryUtil;
import org.example.ray.infrastructure.zk.CuratorClient;
import org.example.ray.util.PropertiesFileUtil;


import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description: server shut down
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerShutdownHook {

    private static final ServerShutdownHook INSTANCE = new ServerShutdownHook();

    public static ServerShutdownHook getInstance() {
        return INSTANCE;
    }

    /**
     * register shut down hook
     */
    public void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // 执行清理操作
            clearAll();
        }));
    }

    private void clearAll() {
        try {
            // 清理注册表
            InetSocketAddress inetSocketAddress = new InetSocketAddress(InetAddress.getLocalHost().getHostAddress(), PropertiesFileUtil.readPortFromProperties());
            CuratorClient.clearRegistry(CuratorClient.getZkClient(), inetSocketAddress);
        } catch (Exception ignored) {

        }
        // 关闭线程池
        ThreadPoolFactoryUtil.shutDownAllThreadPool();
    }

}
