package org.example.ray.infrastructure.compress;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description:
 */
public interface CompressService{

    byte getCompressMethod();

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
