package org.example.ray.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description: use for config file reading
 */


@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    /**
     * rpc config path
     */
    RPC_CONFIG_PATH("rpc.properties"),

    /**
     * zookeeper address
     */
    ZK_ADDRESS("rpc.zookeeper.address"),

    /**
     * Netty port
     */
    NETTY_PORT("netty.port");

    /**
     * property value
     */
    private final String propertyValue;

}
