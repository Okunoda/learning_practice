package org.erywim.chapter1;


import lombok.extern.slf4j.Slf4j;
import org.erywim.ByteBufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

/**
 * @author Erywim 2024/6/14
 */
@Slf4j
public class SelectorServer {
    public static void main(String[] args) throws IOException {
        Selector selector = Selector.open();
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        SelectionKey sscKey = socketChannel.bind(new InetSocketAddress(9090)).configureBlocking(false).register(selector, 0, null);
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("最开始注册的server socket sscKey 是{}", sscKey);
        while (true) {
            //事件在未处理时，他不会阻塞，事件发生后要么处理，要么取消，不然就会一直循环
            selector.select();//阻塞的，只有发生事件才会往下进行
            //处理事件，selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();//拿到所有可用的事件
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                try {
                    log.info("key 是{}", key);
                    //区分事件类型
                    if (key.isAcceptable()) {
                        //是accept类型
                        ServerSocketChannel channel = (ServerSocketChannel) key.channel();//其实这里的channel 就是上面的socketChannel
                        SocketChannel accept = channel.accept();//使用accept简历连接
                        accept.configureBlocking(false);//selector 是需要配合非阻塞模式进行使用的
                        ByteBuffer buffer = ByteBuffer.allocate(16);
                        SelectionKey scKey = accept.register(selector,0,buffer);
                        scKey.interestOps(SelectionKey.OP_READ);
                        log.info("accept：{}" , accept);
                        log.debug("scKey 是 ： {}",scKey.hashCode());
                    }else if (key.isReadable()) {
                        //是read类型
                        SocketChannel channel = (SocketChannel) key.channel();
    //                    channel.configureBlocking(false);
                        ByteBuffer buffer = (ByteBuffer) key.attachment();
                        int read = channel.read(buffer);

                        if(read == -1) {
                            key.cancel();
                        }else{
                            split(buffer);
                            if(buffer.position() == buffer.limit()) {
                                ByteBuffer newBuffer = ByteBuffer.allocate(buffer.capacity() * 2);
                                buffer.flip();
                                newBuffer.put(buffer);
                                key.attach(newBuffer);
                            }
                        }
                    }
                    //处理完一个key之后一定要进行移除操作
                    iterator.remove();
                } catch (IOException e) {
                    e.printStackTrace();
                    key.cancel();
                }
            }
        }
    }

    private static void split(ByteBuffer buffer) {
        buffer.flip();
        for (int i = 0; i < buffer.limit(); i++) {
            if ('\n' == buffer.get(i)) {
                int msgLen = i + 1 - buffer.position();
                ByteBuffer target = ByteBuffer.allocate(msgLen);
                for (int i1 = 0; i1 < msgLen; i1++) {
                    target.put(buffer.get());
                }
                ByteBufferUtil.debugAll(target);
            }
        }
        buffer.compact();
    }
}
