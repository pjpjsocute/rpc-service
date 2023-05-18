package org.example.ray.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author zhoulei
 * @create 2023/5/18
 * @description:
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class RequestDto implements Serializable {



    private static final long serialVersionUID = 4984895755097217640L;

    public Integer input1;

    public Integer input2;

}
