package org.example.ray.infrastructure.config;

import javax.annotation.PostConstruct;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhoulei
 * @create 2023/5/18
 * @description: read Properties from application.yml, solve client and server
 *               start local but port conflict
 */
@Component
@ConfigurationProperties(prefix = "netty.server")
public class PropertiesReader {

    private String                  port;

    private static PropertiesReader instance;

    @PostConstruct
    public void init() {
        instance = this;
    }

    public static Integer getNettyServerPort() {
        return Integer.parseInt(getInstance().getPort());
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public static PropertiesReader getInstance() {
        return instance;
    }
}
