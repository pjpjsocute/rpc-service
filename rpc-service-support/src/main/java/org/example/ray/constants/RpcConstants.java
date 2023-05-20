package org.example.ray.constants;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * @author zhoulei
 * @create 2023/5/17
 * @description: constants
 */
public class RpcConstants {

    /**
     * Magic number. Verify RpcMessage
     */
    public static final byte[]  MAGIC_NUMBER            = {(byte)'g', (byte)'r', (byte)'p', (byte)'c'};

    /**
     * DEFAULT_CHARSET
     */
    public static final Charset DEFAULT_CHARSET         = StandardCharsets.UTF_8;
    /**
     * message protocol version
     */
    public static final byte    VERSION                 = 1;

    /**
     * Request
     */
    public static final byte    REQUEST_TYPE            = 1;

    /**
     * Response
     */
    public static final byte    RESPONSE_TYPE           = 2;

    /**
     * heat request return ping
     */
    public static final byte    HEARTBEAT_REQUEST_TYPE  = 3;

    /**
     * heat response return pong
     */
    public static final byte    HEARTBEAT_RESPONSE_TYPE = 4;

    public static final String  PING                    = "ping";

    public static final String  PONG                    = "pong";

    /**
     * message protocol head length
     */
    public static final int     HEAD_LENGTH             = 16;

    /**
     * load Balance configuration node number
     */
    public static final int     VIRTUAL_NODES           = 320;

    /**
     * LOAD BALANCE way
     */
    public static final String  LOAD_BALANCE            = "hash";

}
