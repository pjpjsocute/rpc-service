package org.example.ray.domain;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    private String            traceId;
    /**
     * interface name
     */
    private String            serviceName;
    /**
     * method name
     */
    private String            methodName;
    /**
     * parameters
     */
    private Object[]          parameters;
    /**
     * parameter types
     */
    private Class<?>[]        paramTypes;
    /**
     * version
     */
    private String            version;
    /**
     * group
     */
    private String            project;

    private String            group;

    /**
     * generate service name,use to distinguish different service,and * can be
     * split to get the service name
     */
    public String fetchRpcServiceName() {
        return this.getProject() + "*" + this.getGroup() + "*" + this.getServiceName() + "*" + this.getVersion();
    }

}
