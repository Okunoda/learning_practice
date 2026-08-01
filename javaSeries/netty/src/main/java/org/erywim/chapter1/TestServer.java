package org.erywim.chapter1;

import org.erywim.ByteBufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Erywim 2024/6/10
 */
public class TestServer {
    public static void main(String[] args) throws IOException {
        //1. 创建连接区
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        //创建服务器后绑定端口
        ServerSocketChannel serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(8080));

        List<SocketChannel> channels = new ArrayList<>();
        while(true){
            System.out.println("等待连接");
            SocketChannel accept = serverChannel.accept();
            System.out.println("连接成功，开始接受数据");
            channels.add(accept);
            for (SocketChannel channel : channels) {
                channel.read(buffer);
                buffer.flip();
                ByteBufferUtil.debugAll(buffer);
                buffer.clear();
            }
        }
    }
}
