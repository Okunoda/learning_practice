package org.erywim.chapter3.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import org.erywim.chapter3.message.RpcRequestMessage;
import org.erywim.chapter3.protocol.MessageCodecSharable;
import org.erywim.chapter3.protocol.SequenceIdGenerator;
import org.erywim.chapter3.server.handler.RpcRequestMessageHandler;
import org.erywim.chapter3.server.handler.RpcResponseMessageHandler;
import org.erywim.chapter3.server.service.HelloService;

import java.lang.reflect.Proxy;

/**
 * @author Erywim 2024/7/30
 */
@Slf4j
public class RpcClientManager {
    private static final Object LOCK = new Object();
    private static Channel channel = null;

    public static void main(String[] args) {
        HelloService service = getProxyClass(HelloService.class);
        System.out.println("service.sayHello(\"zhangsan \") = " + service.sayHello("zhangsan "));
//        service.sayHello("lisi");
    }



    public static <T> T getProxyClass(Class<T> clazz){
        ClassLoader classLoader = clazz.getClassLoader();
        Class<?>[] interfaces = new Class[]{clazz};
                                                                    //     被代理对象某次执行的方法   方法的参数
        Object o = Proxy.newProxyInstance(classLoader, interfaces, (proxy, method, args) -> {
            int sequenceId = SequenceIdGenerator.nextId();//ctrl + alt + v 提取变量
            //1. 把方法构建成协议格式
            RpcRequestMessage message = new RpcRequestMessage(
                    sequenceId,
                    clazz.getName(),
                    method.getName(),
                    method.getReturnType(),
                    method.getParameterTypes(),
                    args
            );
            //2. 在channel中将数据写出
            getChannel().writeAndFlush(message);
            //3. 创建promise等待结果                                指定 promise 异步接收结果的线程
            DefaultPromise<Object> promise = new DefaultPromise<>(getChannel().eventLoop());
            RpcResponseMessageHandler.PROMISES.put(sequenceId,promise);

            //4.当前线程等待 promise 结果
            promise.await();
            if (promise.isSuccess()) {
                return promise.get();
            }else{
                throw new RuntimeException(promise.cause());
            }
        });
        return (T) o;
    }


    public static Channel getChannel(){
        if(channel != null){
            return channel;
        }
        synchronized (LOCK){
            if(channel != null){
                return channel;
            }
            initChannel();
            return channel;
        }
    }



    public static void initChannel() {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler LOGGING_HANDLER = new LoggingHandler(LogLevel.DEBUG);
        MessageCodecSharable MESSAGE_CODEC = new MessageCodecSharable();

        // rpc 响应消息处理器，待实现
        RpcResponseMessageHandler RPC_HANDLER = new RpcResponseMessageHandler();
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
        try {
            channel = bootstrap.connect("localhost", 8123).sync().channel();
            channel.closeFuture().addListener(future -> {
                group.shutdownGracefully();
            });
        } catch (Exception e) {
            log.error("client error", e);
        }
    }
}
