package org.erywim.chapter3.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.erywim.chapter3.message.ChatRequestMessage;
import org.erywim.chapter3.message.ChatResponseMessage;
import org.erywim.chapter3.server.session.SessionFactory;

/**
 * @author Erywim 2024/7/26
 */
@ChannelHandler.Sharable
public class ChatRequestMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ChatRequestMessage msg) throws Exception {
        Channel channel = SessionFactory.getSession().getChannel(msg.getTo());
        if(channel != null) {
            channel.writeAndFlush(new ChatResponseMessage(msg.getFrom(),msg.getContent()));
        }else{
            ctx.writeAndFlush(new ChatResponseMessage(false, "对方用户不存在或不在线！"));
        }
    }
}
