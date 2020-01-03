package org.tinygame.herostory;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

/**
 * 广播器
 */
public final class BraoadCaster {

    /**
     * 客户端信息数组，一定要用static,否则无法实现群发
     */
    private static final ChannelGroup channelGrroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * 私有化类默认构造器
     */
    private BraoadCaster() {
    }

    /**
     * 添加信道
     *
     * @param channel
     */
    public static void addChannel(Channel channel) {
        channelGrroup.add(channel);
    }

    /**
     * 移除信道
     *
     * @param channel
     */
    public static void removeChannel(Channel channel) {
        channelGrroup.remove(channel);
    }

    /**
     * 广播消息
     *
     * @param msg
     */
    public static void broadCast(Object msg) {
        if (msg == null) {
            return;
        }
        channelGrroup.writeAndFlush(msg);
    }
}
