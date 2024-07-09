package org.erywim.chapter1;

import org.erywim.ByteBufferUtil;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author Erywim 2024/7/8
 */
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress("127.0.0.1", 8888));

        //创建boss接受连接
        Selector boss = Selector.open();
        serverSocketChannel.register(boss, SelectionKey.OP_ACCEPT);

        //创建worker处理连接
        Worker worker = new Worker("worker-01");

        while (true) {
            boss.select();
            Iterator<SelectionKey> iterator = boss.selectedKeys().iterator();
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if (key.isAcceptable()) {
                    SocketChannel socketChannel = serverSocketChannel.accept();
                    socketChannel.configureBlocking(false);

                    //关联selector
                    worker.register(socketChannel);

                }
            }
        }
    }

    private static class Worker implements Runnable{
        private Thread thread;
        private Selector selector;
        private final String name;
        private boolean start = false;
        private ConcurrentLinkedQueue<Runnable> queue = new ConcurrentLinkedQueue<>();

        public Worker(String name) {
            this.name = name;
        }

        public void register(SocketChannel channel) throws IOException {
            if(!start){
                this.thread = new Thread(this,name);
                this.selector = Selector.open();
                thread.start();
            }
            queue.add(()->{     //这里只是放进去，并没有执行
                try {
                    channel.register(selector,SelectionKey.OP_READ);
                } catch (ClosedChannelException e) {
                    throw new RuntimeException(e);
                }
            });
            selector.wakeup();//唤醒 select ，否则会阻塞在selector.select()处，这里的唤醒机制和LockSupport类似，是票据式的
        }

        @Override
        public void run() {
            while(true){
                try {
                    selector.select();  //worker-01 阻塞
                    Runnable runnable = queue.poll();
                    if(runnable != null ){
                        runnable.run(); // 真正执行了 channel.register(selector,SelectionKey.OP_READ);
                    }
                    Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                    while (iterator.hasNext()) {
                        SelectionKey key = iterator.next();
                        iterator.remove();
                        if (key.isReadable()) {
                            SocketChannel channel = (SocketChannel) key.channel();
                            ByteBuffer buffer = ByteBuffer.allocate(256);
                            channel.read(buffer);
                            buffer.flip();
                            ByteBufferUtil.debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            }
        }
    }
}
