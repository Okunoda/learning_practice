package org.erywim.chapter2;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;

import java.net.InetSocketAddress;

/**
 * @author Erywim 2024/7/9
 */
public class NettyClientHello {
    public static void main(String[] args) throws InterruptedException {
        //1. 创建启动类
        new Bootstrap()
                //2. 添加 EventLoopGroup
                .group(new NioEventLoopGroup())
                //3. 选择客户端 socketChannel 实现
                .channel(NioSocketChannel.class)
                //4. 添加处理器
                .handler(
                    //5. 初始化channel
                    new ChannelInitializer<NioSocketChannel>() {
                    @Override //这个方法在连接建立之后被调用
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        nioSocketChannel.pipeline().addLast(new StringEncoder());

                    }
                })
                //6. 连接到服务器
                .connect(new InetSocketAddress("127.0.0.1", 8888))
                .sync()
                .channel()
                .writeAndFlush("hello world！");
    }
}
