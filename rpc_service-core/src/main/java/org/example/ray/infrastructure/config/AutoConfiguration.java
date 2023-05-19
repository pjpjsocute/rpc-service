package org.example.ray.infrastructure.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhoulei
 * @create 2023/5/19
 * @description:
 */
@Configuration
@ComponentScan(value = {"org.example.ray.infrastructure"})
public class AutoConfiguration {}
