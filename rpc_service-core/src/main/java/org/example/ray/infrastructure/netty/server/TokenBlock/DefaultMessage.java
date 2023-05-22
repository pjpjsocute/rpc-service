package org.example.ray.infrastructure.netty.server.TokenBlock;

import io.netty.channel.ChannelHandlerContext;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @author zhoulei
 * @create 2023/5/22
 * @description:
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DefaultMessage {

     ChannelHandlerContext ctx;

     Object                msg;


}
