package com.dalididilo.netty.aio.client;


import com.dalididilo.netty.aio.ChannelAdapter;
import com.dalididilo.netty.aio.ChannelHandler;

import java.nio.channels.AsynchronousSocketChannel;
import java.nio.charset.Charset;

/**
 * 客户端消息处理器。
 * @author dalididilo
 * @date 2020-10-28 14:31:57
 */
public class AioClientHandler extends ChannelAdapter {
    public AioClientHandler(AsynchronousSocketChannel channel, Charset charset) {
        super(channel, charset);
    }

    @Override
    public void channelActive(ChannelHandler ctx) {

    }

    @Override
    public void channelInactive(ChannelHandler ctx) {

    }

    @Override
    public void channelRead(ChannelHandler ctx) {

    }
}
