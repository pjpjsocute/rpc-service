package org.example.ray.consumer.controller;

import org.example.ray.annotation.RpcConsumer;
import org.example.ray.api.TestInterface;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhoulei
 * @create 2023/5/18
 * @description:
 */
@RestController
@RequestMapping("/test")
public class TestControllerSeco {

    @RpcConsumer(group = "test2", version = "1.0")
    private TestInterface testInterface;

    @GetMapping("test2")
    public String  queryContentSnapShot(String input) {
        return testInterface.testGetString(input);
    }

}
