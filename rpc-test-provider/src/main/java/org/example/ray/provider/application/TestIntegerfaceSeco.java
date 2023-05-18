package org.example.ray.provider.application;

import org.example.ray.annotation.RpcProvider;
import org.example.ray.api.TestInterface;
import org.example.ray.model.dto.ResponseDto;
import org.example.ray.model.request.RequestDto;

/**
 * @author zhoulei
 * @create 2023/5/18
 * @description:
 */
@RpcProvider(group = "test2", version = "1.0")
public class TestIntegerfaceSeco implements TestInterface {
    @Override
    public String testGetString(String name) {
        return name + "seocnd interface";
    }

    @Override
    public ResponseDto testGetDto(RequestDto requestDto) {
        ResponseDto responseDto = ResponseDto.builder()
                .addResult(requestDto.getInput1() + requestDto.getInput2() + "Second interface")
                .multipleResult(requestDto.getInput1() * requestDto.getInput2() + "Second interface")
                .build();
        return responseDto;
    }
}