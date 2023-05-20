package org.example.ray.domain;

import java.io.Serializable;

import org.example.ray.enums.RpcResponseCodeEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class RpcResponse<T> implements Serializable {

    private static final long serialVersionUID = 347966260947189201L;
    /**
     * request id
     */
    private String            requestId;
    /**
     * response code
     */
    private Integer           code;
    /**
     * response message
     */
    private String            message;
    /**
     * response body
     */
    private T                 data;

    /**
     * success
     * @param data
     * @param requestId
     * @return
     * @param <T>
     */
    public static <T> RpcResponse<T> success(T data, String requestId) {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.SUCCESS.getCode());
        response.setMessage(RpcResponseCodeEnum.SUCCESS.getMessage());
        response.setRequestId(requestId);
        if (null != data) {
            response.setData(data);
        }
        return response;
    }

    /**
     * fail
     * @return
     * @param <T>
     */
    public static <T> RpcResponse<T> fail() {
        RpcResponse<T> response = new RpcResponse<>();
        response.setCode(RpcResponseCodeEnum.FAIL.getCode());
        response.setMessage(RpcResponseCodeEnum.FAIL.getMessage());
        return response;
    }

}
