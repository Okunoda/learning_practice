package org.erywim.chapter3.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.erywim.chapter3.message.*;
import org.erywim.chapter3.protocol.MessageCodecSharable;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Erywim 2024/7/24
 */
@Slf4j
public class ChatClient {
    public static void main(String[] args) {
        NioEventLoopGroup group = new NioEventLoopGroup();
        LoggingHandler loggingHandler = new LoggingHandler();
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        ChannelFuture channelFuture = null;
        CountDownLatch countDownLatch = new CountDownLatch(1);
        AtomicBoolean LOGIN = new AtomicBoolean();

        try {
            channelFuture = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline()
                                    .addLast(new LengthFieldBasedFrameDecoder(1024, 12, 4, 0, 0)) // 固定帧长解析器，注意不能用单一实例，否则多线程并发时会出错
                                    .addLast(loggingHandler)
                                    .addLast(messageCodecSharable)
                                    //IdleStateHandler是用来判断是不是 读空闲事件过长 或 写空闲事件过长
                                    // 5s 内如果没有收到 channel 的数据，会触发一个 IdleState#WRITER_IDLE 事件
                                    .addLast(new IdleStateHandler(0,3,0))
                                    .addLast(new ChannelDuplexHandler(){
                                        //用来处理一些特殊事件
                                        @Override
                                        public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
                                            IdleStateEvent ev = (IdleStateEvent) evt;
                                            //触发了写空闲事件
                                            if (ev.state().equals(IdleState.WRITER_IDLE)) {
//                                                log.info("3s 没有向服务器发送数据了，发送心跳包");
                                                ctx.channel().writeAndFlush(new PingMessage());
                                            }
                                        }
                                    })
                                    .addLast("clientHandler", new ChannelInboundHandlerAdapter() {
                                        //在连接建立之后发送登入信息
                                        @Override
                                        public void channelActive(ChannelHandlerContext ctx) throws Exception {
                                            new Thread(() -> {
                                                Scanner scanner = new Scanner(System.in);
                                                System.out.println("输入名称");
                                                String userName = scanner.nextLine();
                                                System.out.println("输入密码");
                                                String pawd = scanner.nextLine();
                                                LoginRequestMessage loginRequestMessage = new LoginRequestMessage(userName, pawd);
                                                ctx.writeAndFlush(loginRequestMessage);

                                                System.out.println("等待进一步输入");
                                                try {
                                                    countDownLatch.await();
                                                } catch (InterruptedException e) {
                                                    throw new RuntimeException(e);
                                                }
                                                if (!LOGIN.get()) {
                                                    ctx.channel().close();
                                                    return;
                                                }
                                                while (true) {
                                                    System.out.println("==================================");
                                                    System.out.println("send [username] [content]");
                                                    System.out.println("gsend [group name] [content]");
                                                    System.out.println("gcreate [group name] [m1,m2,m3...]");
                                                    System.out.println("gmembers [group name]");
                                                    System.out.println("gjoin [group name]");
                                                    System.out.println("gquit [group name]");
                                                    System.out.println("quit");
                                                    System.out.println("==================================");
                                                    String cmd = scanner.nextLine();
                                                    String[] cmdArr = cmd.split(" ");

                                                    switch (cmdArr[0]) {
                                                        case "send":
                                                            ctx.writeAndFlush(new ChatRequestMessage(userName, cmdArr[1], cmdArr[2]));
                                                            break;
                                                        case "gsend":
                                                            ctx.writeAndFlush(new GroupChatRequestMessage(userName, cmdArr[1], cmdArr[2]));
                                                            break;
                                                        case "gcreate":
                                                            HashSet<String> memberSet = new HashSet<>(Arrays.asList(cmdArr[2].split(",")));
                                                            memberSet.add(userName);
                                                            ctx.writeAndFlush(new GroupCreateRequestMessage(cmdArr[1], memberSet));
                                                            break;
                                                        case "gmembers":
                                                            ctx.writeAndFlush(new GroupMembersRequestMessage(cmdArr[1]));
                                                            break;
                                                        case "gjoin":
                                                            ctx.writeAndFlush(new GroupJoinRequestMessage(userName, cmdArr[1]));
                                                            break;
                                                        case "gquit":
                                                            ctx.writeAndFlush(new GroupQuitRequestMessage(userName, cmdArr[1]));
                                                            break;
                                                        case "quit":
                                                            ctx.channel().close();
                                                            return;
                                                        default:
                                                    }
                                                }

                                            }, "system in").start();
                                        }

                                        //接收响应信息
                                        @Override
                                        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                                            System.out.println("msg = " + msg);
                                            if (msg instanceof LoginResponseMessage) {
                                                countDownLatch.countDown();
                                                LoginResponseMessage response = (LoginResponseMessage) msg;
                                                LOGIN.set(response.isSuccess());
                                            }
                                        }
                                    });
                        }
                    }).connect("localhost", 8123).sync();
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
