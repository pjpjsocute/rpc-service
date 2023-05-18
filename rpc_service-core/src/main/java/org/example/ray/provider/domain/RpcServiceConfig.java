package org.example.ray.provider.domain;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcServiceConfig {
    /**
     * service version
     */
    private String version = "";

    /**
     * target service
     */
    private Object service;

    /**
     * belong to which project
     */
    private String project = "";

    /**
     * group
     */
    private String group = "";

    public String getRpcServiceName() {
        return this.getProject() +"*"+this.getGroup()+"*"+ this.getServiceName() +"*"+ this.getVersion();
    }

    /**
     * get the interface name
     * @return
     */
    public String getServiceName() {
        return this.service.getClass().getInterfaces()[0].getCanonicalName();
    }

    /**
     * get the class name use for search
     * @return
     */
    public String getClassName(){
        return this.service.toString();
    }
}
