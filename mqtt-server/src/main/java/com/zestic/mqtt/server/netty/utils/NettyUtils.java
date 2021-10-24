package com.zestic.mqtt.server.netty.utils;

import com.zestic.core.util.StrUtil;
import io.netty.channel.Channel;
import io.netty.util.AttributeKey;

import java.net.InetSocketAddress;

public final class NettyUtils {
    private static final String CLIENT_ID = "clientId";
    private static final AttributeKey<String> ATTR_CLIENT_ID = AttributeKey.valueOf(CLIENT_ID);
    private static final String USER_NAME = "userName";
    public static final AttributeKey<String> ATTR_USER_NAME = AttributeKey.valueOf(USER_NAME);

    private NettyUtils() {
    }

    public static void clientInfo(Channel channel, String clientId, String userName) {
        clientId(channel, clientId);

        userName(channel, userName);
    }

    public static void clientId(Channel channel, String clientId) {
        channel.attr(NettyUtils.ATTR_CLIENT_ID).set(clientId);
    }

    public static String clientId(Channel channel) {
        return channel.attr(NettyUtils.ATTR_CLIENT_ID).get();
    }

    public static void userName(Channel channel, String userName) {
        channel.attr(ATTR_USER_NAME).set(userName);
    }

    public static String userName(Channel channel) {
        return channel.attr(ATTR_USER_NAME).get();
    }

    public static String getRemoteIp(Channel channel) {
        try {
            final InetSocketAddress socketAddr = (InetSocketAddress) channel.remoteAddress();
            return socketAddr.getAddress().getHostAddress();
        } catch (Throwable t) {
            //TODO 暂时忽略
        }

        return StrUtil.EMPTY;
    }
}
