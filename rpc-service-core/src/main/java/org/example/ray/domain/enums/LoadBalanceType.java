package org.example.ray.domain.enums;

import lombok.Getter;

/**
 * @author zhoulei
 * @create 2023/5/18
 * @description:
 */
@Getter
public enum LoadBalanceType {

    /**
     * Random load balance type.
     */
    RANDOM("random"),
    /**
     * Round robin load balance type.
     */
    ROUND_ROBIN("roundRobin"),
    /**
     * Weight round robin load balance type.
     */
    WEIGHT_ROUND_ROBIN("weightRoundRobin"),
    /**
     * Hash load balance type.
     */
    HASH("hash"),
    /**
     * Weight random load balance type.
     */
    WEIGHT_RANDOM("weightRandom");

    private final String name;

    LoadBalanceType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static LoadBalanceType getLoadBalanceType(String name) {
        for (LoadBalanceType loadBalanceType : LoadBalanceType.values()) {
            if (loadBalanceType.getName().equals(name)) {
                return loadBalanceType;
            }
        }
        return null;
    }
}
