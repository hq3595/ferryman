package com.dahua.ferryman.common.utils;

import io.netty.channel.Channel;

import java.net.SocketAddress;

/**
 * @Author: HuangQiang
 * @Date: 2022/1/23 上午11:06
 */
public class RemotingHelper {
    public static String parseChannelRemoteAddr(final Channel channel) {
        if (null == channel) {
            return "";
        }
        SocketAddress remote = channel.remoteAddress();
        final String addr = remote != null ? remote.toString() : "";

        if (addr.length() > 0) {
            int index = addr.lastIndexOf("/");
            if (index >= 0) {
                return addr.substring(index + 1);
            }

            return addr;
        }

        return "";
    }
}
