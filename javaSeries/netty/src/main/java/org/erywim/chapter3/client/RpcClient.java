package org.erywim.chapter3.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.erywim.chapter3.message.RpcRequestMessage;
import org.erywim.chapter3.protocol.MessageCodecSharable;
import org.erywim.chapter3.server.handler.RpcResponseMessageHandler;

/**
 * @author Erywim 2024/7/30
 */
@Slf4j
public class RpcClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        // rpc 响应消息处理器，待实现
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.group(group);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0)); // 固定帧长解析器，注意不能用单一实例，否则多线程并发时会出错                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(LOGGING_HANDLER);
                    ch.pipeline().addLast(MESSAGE_CODEC);
                    ch.pipeline().addLast(RPC_HANDLER);
                }
            });
            Channel channel = bootstrap.connect("localhost", 8123).sync().channel();

            RpcRequestMessage message = new RpcRequestMessage(
                    1,
                    "org.erywim.chapter3.server.service.HelloService",
                    "sayHello",
                    String.class,
                    new Class[]{String.class},
                    new Object[]{"张三"}
            );

            ChannelFuture channelFuture = channel.writeAndFlush(message)
                    //其实这里的promise就是channelFuture对象
                    .addListener(promise -> {
                        //这里是一个调试技巧，谁的问题谁承担责任，这里是writeAndFlush 出现了问题，就应该盯着它的结果进行判断
                        if(!promise.isSuccess()) {
                            Throwable cause = promise.cause();
                            log.error(cause.getMessage(), cause);
                        }
                    });


            channel.closeFuture().sync();
        } catch (Exception e) {
            log.error("client error", e);
        } finally {
            group.shutdownGracefully();
        }
    }
}