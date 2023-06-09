package org.example.ray.infrastructure.netty.client;

import io.netty.channel.Channel;
import org.example.ray.util.LogUtil;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author zhoulei
 * @create 2023/5/16
 * @description:
 */


public class AddressChannelManager {

    private final Map<String, Channel> channelMap;

    public AddressChannelManager() {
        this.channelMap = new ConcurrentHashMap<>();
    }

    public Channel get(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        // determine if there is a connection for the corresponding address
        if (channelMap.containsKey(key)) {
            Channel channel = channelMap.get(key);
            // if so, determine if the connection is available, and if so, get it directly
            if (channel != null && channel.isActive()) {
                return channel;
            } else {
                channelMap.remove(key);
            }
        }
        return null;
    }

    public void set(InetSocketAddress inetSocketAddress, Channel channel) {
        String key = inetSocketAddress.toString();
        channelMap.put(key, channel);
    }

    public void remove(InetSocketAddress inetSocketAddress) {
        String key = inetSocketAddress.toString();
        channelMap.remove(key);
        LogUtil.info("Channel map size :[{}]", channelMap.size());
    }
}
