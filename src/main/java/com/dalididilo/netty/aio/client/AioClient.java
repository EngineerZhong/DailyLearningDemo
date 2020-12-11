package com.dalididilo.netty.aio.client;


import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.concurrent.Future;

/**
 * 客户端
 * AIO 异步非阻塞I/O模型。
 * @author dalididilo
 * @date 2020-10-28 14:31:57
 *
 * AIO是在NIO（同步非阻塞模型）的基础上引入了新的异步通道概念
 * 提供了异步文件通道和异步套接字通道的实现。
 */
public class AioClient {

    public static void main(String[] args) throws Exception {
        AsynchronousSocketChannel socketChannel = AsynchronousSocketChannel.open();
        Future<Void> future = socketChannel.connect(new InetSocketAddress("192.168.1.93",7397));
        future.get();
//        socketChannel.read(ByteBuffer.allocate(1024),null,new )


    }


}
