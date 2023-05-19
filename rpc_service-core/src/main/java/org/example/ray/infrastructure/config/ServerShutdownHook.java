package org.example.ray.infrastructure.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.ray.infrastructure.netty.server.NettyServer;
import org.example.ray.infrastructure.util.ThreadPoolFactoryUtil;
import org.example.ray.infrastructure.zk.util.CuratorUtils;
import org.example.ray.util.PropertiesFileUtil;


import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServerShutdownHook {

    private static final ServerShutdownHook INSTANCE = new ServerShutdownHook();

    public static ServerShutdownHook getInstance() {
        return INSTANCE;
    }

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
            CuratorUtils.clearRegistry(CuratorUtils.getZkClient(), inetSocketAddress);
        } catch (Exception ignored) {

        }
        // 关闭线程池
        ThreadPoolFactoryUtil.shutDownAllThreadPool();
    }

}
