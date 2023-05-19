package org.example.ray.consumer.controller;

import org.example.ray.annotation.RpcConsumer;
import org.example.ray.api.TestInterface;
import org.example.ray.model.dto.ResponseDto;
import org.example.ray.model.request.RequestDto;
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

    @RpcConsumer(group = "test2", version = "1.5")
    private TestInterface testInterface;

    @GetMapping("test2")
    public String queryContentSnapShot(String input) {
        return testInterface.testGetString(input);
    }

    @GetMapping("test3")
    public ResponseDto queryContentSnapShot(Integer input1, Integer input2) {
        RequestDto build = RequestDto.builder().input1(input1).input2(input2).build();
        ResponseDto responseDto = testInterface.testGetDto(build);
        return responseDto;
    }

}
