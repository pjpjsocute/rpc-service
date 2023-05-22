package org.example.ray.domain.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:  ServiceRegistry type
 */
@AllArgsConstructor
@Getter
public enum ServiceRegistryEnum {

    /**
     * zookeeper
     */
    ZK("zk");

    private final String name;
}
