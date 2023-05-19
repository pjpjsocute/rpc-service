package org.example.ray.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description: request sending type
 */
@AllArgsConstructor
@Getter
public enum RpcRequestSendingEnum {
    /**
     * http
     */
    HTTP("http"),

    /**
     * netty
     */
    NETTY("netty"),

    /**
     * socket
     */
    SOCKET("socket");

    private final String name;
}
