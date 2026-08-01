package org.erywim.chapter4;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * @author Erywim 2024/7/29
 */
public class TestTimeoutConncect {
    public static void main(String[] args) throws InterruptedException {
        NioEventLoopGroup group;
        group = new NioEventLoopGroup();
        try {
            ChannelFuture channelFuture = new Bootstrap()
                    .group(group)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS,100)
                    .channel(NioSocketChannel.class)
                    .handler(new LoggingHandler())
                    .connect(new InetSocketAddress("180.123.123.123", 223)).sync();
            channelFuture.channel().close().sync();
        }catch (Exception e){
            e.printStackTrace();
        } finally{
            group.shutdownGracefully();
        }
    }
}
