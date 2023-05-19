package org.example.ray.infrastructure.compress;

import org.example.ray.infrastructure.spi.SPI;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
@SPI
public interface CompressService{

    /**
     * @param bytes
     * @return
     */
    byte[] compress(byte[] bytes);


    /**
     * @param bytes
     * @return
     */
    byte[] decompress(byte[] bytes);
}
