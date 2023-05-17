package org.example.ray.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.io.Serializable;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RpcRequest implements Serializable {

    private static final long serialVersionUID = 8509587559718339795L;
    /**
     * traceId
     */
    private String traceId;
    /**
     * interface name
     */
    private String interfaceName;
    /**
     * method name
     */
    private String methodName;
    /**
     * parameters
     */
    private Object[] parameters;
    /**
     * parameter types
     */
    private Class<?>[] paramTypes;
    /**
     * version
     */
    private String version;
    /**
     * group
     */
    private String project;

    public String fetchRpcServiceName() {
        return this.project+ this.getInterfaceName() +  this.getVersion();
    }


}
