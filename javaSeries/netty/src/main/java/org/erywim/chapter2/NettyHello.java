package org.erywim.chapter2;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Erywim 2024/7/9
 */
@Slf4j
public class NettyHello {
    //--add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/sun.net.util=ALL-UNNAMED --add-opens java.base/java.lang.reflect=ALL-UNNAMED -Dio.netty.tryReflectionSetAccessible=true --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens java.base/jdk.internal.misc=ALL-UNNAMED
    public static void main(String[] args) {
        //1. 创建 bootstrap 启动器，启动服务器
        new ServerBootstrap()
                //2. 类似之前样例中的 BoosEventLoopGroup ，WorkerEventLoopGroup ，是一个selector + 一个线程  ， todo---group 组的概念有些不清楚
                .group(new NioEventLoopGroup())
                //3. 选择 服务器的 ServerSocketChannel 实现
                .channel(NioServerSocketChannel.class)// 除了 Nio 还有 OIO BIO等实现
                //4. boss 负责处理连接 worker(child) 负责处理具体连接的读写，(handler) 决定了 worker(child) 能执行哪些操作
                .childHandler(
                    //5. channel 代表和客户端进行数据读写的通道 ， Initializer 初始化，负责添加别的 handler
                    //这个初始化器只是添加了，只有真正的accept事件发生了才会进行执行
                    new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel nioSocketChannel) throws Exception {
                        //6. 添加具体 handler
                        nioSocketChannel.pipeline().addLast(new StringDecoder());//将ByteBuf转换为字符串
                        nioSocketChannel.pipeline().addLast(new ChannelInboundHandlerAdapter() { //自定义的handler

                            @Override//读事件
                            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                System.out.println(msg);
                            }
                        });

                    }
                })
                //7. 绑定端口
                .bind(8888);
    }
}
