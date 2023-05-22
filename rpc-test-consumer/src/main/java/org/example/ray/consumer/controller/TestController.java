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
    public String queryContentSnapShot(String input) {
        return testInterface.testGetString(input);
    }

    @GetMapping("test4")
    public String multithreadedTest(String input) {
        for (int i = 0; i < 100; i++) {
            new Thread(() -> {
                for (int j = 0; j < 100; j++){
                    testInterface.testGetString(input);
                }
            }).start();
        }
        return "success";
    }

}
