package org.example.ray.test;

import org.example.ray.annotation.RpcProvider;

/**
 * @author zhoulei
 * @create 2023/5/18
 * @description:
 */
@RpcProvider(serviceName = "Test",group = "test", version = "1.0")
public class TestImpl implements Test{
    @Override
    public void test() {

    }
}
