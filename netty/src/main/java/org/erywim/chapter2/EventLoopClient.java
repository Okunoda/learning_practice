package org.erywim.chapter2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Scanner;

/**
 * @author Erywim 2024/7/15
 */
@Slf4j
public class EventLoopClient {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        ChannelFuture channelFuture = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new LoggingHandler())
                                .addLast(new StringDecoder());
                    }
                }).connect(new InetSocketAddress("127.0.0.1", 8888));
        Channel channel = channelFuture.sync().channel();
        log.info("{}",channel);
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            String next = scanner.next();
            if ("q".equals(next)) {
                channel.close();
            }
        },"closeChannel").start();

        //获取channelFuture对象，可以使用两种方式进行关闭
        //方式1. 同步关闭
        log.info("等待channel关闭");
        channelFuture.sync();
        log.info("关闭channel之后的操作");

        //方式2. 通过添加监听器进行异步关闭
        channelFuture.addListener((ChannelFutureListener) future -> {
            log.info("处理关闭完channel之后的操作");
            group.shutdownGracefully();//这是将eventLoopGroup进行关闭，关闭之后client线程就会停止了
        });

    }
}
