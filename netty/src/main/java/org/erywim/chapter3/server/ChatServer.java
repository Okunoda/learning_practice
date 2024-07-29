package org.erywim.chapter3.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.erywim.chapter3.server.handler.ChatRequestMessageHandler;
import org.erywim.chapter3.server.handler.GroupChatRequestMessageHandler;
import org.erywim.chapter3.server.handler.GroupCreateRequestMessageHandler;
import org.erywim.chapter3.server.handler.LoginRequestMessageHandler;
import org.erywim.chapter3.protocol.MessageCodecSharable;

import java.net.InetSocketAddress;


/**
 * @author Erywim 2024/7/23
 */
@Slf4j
public class ChatServer {
    public static void main(String[] args) {
        NioEventLoopGroup boss = new NioEventLoopGroup();
        NioEventLoopGroup worker = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler();
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        LoginRequestMessageHandler loginRequestMessageHandler = new LoginRequestMessageHandler();
        ChatRequestMessageHandler chatRequestMessageHandler = new ChatRequestMessageHandler();
        GroupCreateRequestMessageHandler groupCreateRequestMessageHandler = new GroupCreateRequestMessageHandler();
        GroupChatRequestMessageHandler groupChatRequestMessageHandler = new GroupChatRequestMessageHandler();
        try {
            ChannelFuture channelFuture = new ServerBootstrap().group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {

                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0)) // 固定帧长解析器，注意不能用单一实例，否则多线程并发时会出错
                                    .addLast(loggingHandler)
                                    .addLast(messageCodecSharable)
                                    .addLast(loginRequestMessageHandler)
                                    .addLast(chatRequestMessageHandler)
                                    .addLast(groupCreateRequestMessageHandler)
                                    .addLast(groupChatRequestMessageHandler);
                        }
                    }).bind(new InetSocketAddress("127.0.0.1", 8123))
                    .sync();

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }

    }

}
