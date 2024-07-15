package org.erywim.chapter2;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Erywim 2024/7/15
 */
@Slf4j
public class EmbeddedChannelTest {
    public static void main(String[] args) {
        ChannelInboundHandlerAdapter inboundAdapter1 = new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.info("1");
                ctx.fireChannelRead(msg);
            }
        };
        ChannelInboundHandlerAdapter inboundAdapter2 = new ChannelInboundHandlerAdapter(){
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                log.info("2");
                System.out.println("msg = " + msg);
            }
        };
        ChannelOutboundHandlerAdapter outboundAdapter1 = new ChannelOutboundHandlerAdapter(){
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.info("3");
                super.write(ctx, msg, promise);
            }
        };
        ChannelOutboundHandlerAdapter outboundAdapter2 = new ChannelOutboundHandlerAdapter(){
            @Override
            public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
                log.info("4");
                super.write(ctx, msg, promise);
            }
        };

        EmbeddedChannel channel = new EmbeddedChannel(inboundAdapter1, inboundAdapter2, outboundAdapter1, outboundAdapter2);
        //模拟数据入栈的操作
//        channel.writeInbound("hello");
        //模拟数据出栈的操作
        channel.writeOutbound("world");
    }
}
