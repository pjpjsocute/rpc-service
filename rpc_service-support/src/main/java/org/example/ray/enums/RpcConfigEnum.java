package org.example.ray.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RpcConfigEnum {

    RPC_CONFIG_PATH("rpc.properties"),

    ZK_ADDRESS("rpc.zookeeper.address"),

    SPRING_CONFIG_PATH("application.yml"),

    NETTY_PORT("NETTY.PORT");

    private final String propertyValue;

}
