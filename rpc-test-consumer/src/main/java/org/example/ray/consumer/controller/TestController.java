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
public class TestController {

    @RpcConsumer
    private TestInterface testInterface;

    @GetMapping("test1")
    public String  queryContentSnapShot(String input) {
        return testInterface.testGetString(input);
    }

}
