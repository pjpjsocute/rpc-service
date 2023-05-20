package org.example.ray.api;

import org.example.ray.model.dto.ResponseDto;
import org.example.ray.model.request.RequestDto;

/**
 * @author zhoulei
 * @create 2023/5/18
 * @description:
 */
public interface TestInterface {

    /**
     * get string
     * @param name
     * @return
     */
    String testGetString(String name);

    /**
     * get dto
     * @param requestDto
     * @return
     */
     ResponseDto testGetDto(RequestDto requestDto);
}
