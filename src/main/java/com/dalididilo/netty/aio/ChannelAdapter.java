package com.dalididilo.netty.aio;


import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.Charset;

/**
 * @author dalididilo
 * @date 2020-10-28 14:39:58
 */
public abstract class ChannelAdapter implements CompletionHandler<Integer,Object> {

    private AsynchronousSocketChannel channel;
    private Charset charset;

    public ChannelAdapter(AsynchronousSocketChannel channel, Charset charset) {
        this.channel = channel;
        this.charset = charset;
        if (channel.isOpen()) {
//            channelActive(new ChannelHandler(channel,charset));
        }
    }

    @Override
    public void completed(Integer result, Object attachment) {

    }

    @Override
    public void failed(Throwable exc, Object attachment) {

    }

    public abstract void channelActive(ChannelHandler ctx);
    public abstract void channelInactive(ChannelHandler ctx);

    /**
     * 读取信息抽象类。
     * @param ctx
     */
    public abstract void channelRead(ChannelHandler ctx);
}
