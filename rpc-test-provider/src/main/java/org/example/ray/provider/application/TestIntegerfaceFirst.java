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
@RpcProvider(group = "test", version = "1.0")
public class TestIntegerfaceFirst implements TestInterface {
    @Override
    public String testGetString(String name) {
        return name ;
    }

    @Override
    public ResponseDto testGetDto(RequestDto requestDto) {
        ResponseDto responseDto = ResponseDto.builder()
            .addResult(requestDto.getInput1() + requestDto.getInput2()+"")
            .multipleResult(requestDto.getInput1() * requestDto.getInput2()+"")
            .build();
        return responseDto;
    }
}
