package org.erywim.chapter3.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.erywim.chapter3.server.handler.*;
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
        QuitHandler quitHandler = new QuitHandler();
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
                                    //IdleStateHandler是用来判断是不是 读空闲事件过长 或 写空闲事件过长
                                    // 5s 内如果没有收到 channel 的数据，会触发一个 IdleState#READER_IDLE 事件
                                    .addLast(new IdleStateHandler(5,0,0))
                                    .addLast(new ChannelDuplexHandler(){
                                        //用来处理一些特殊事件
                                        @Override
                                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                            IdleStateEvent event = (IdleStateEvent) evt;
                                            //触发了读空闲事件
                                            if (event.state().equals(IdleState.READER_IDLE)) {
                                                log.info("已经 5s 没有读取到数据了");
                                            }
                                        }
                                    })
                                    .addLast(loginRequestMessageHandler)
                                    .addLast(chatRequestMessageHandler)
                                    .addLast(groupCreateRequestMessageHandler)
                                    .addLast(groupChatRequestMessageHandler)
                                    .addLast(quitHandler);
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
