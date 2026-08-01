package org.erywim.chapter3.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.erywim.chapter3.message.GroupChatRequestMessage;
import org.erywim.chapter3.message.GroupChatResponseMessage;
import org.erywim.chapter3.server.session.GroupSession;
import org.erywim.chapter3.server.session.GroupSessionFactory;

import java.util.List;

/**
 * @author Erywim 2024/7/29
 */
@ChannelHandler.Sharable
public class GroupChatRequestMessageHandler extends SimpleChannelInboundHandler<GroupChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupChatRequestMessage msg) throws Exception {
        List<Channel> membersChannel = GroupSessionFactory.getGroupSession().getMembersChannel(msg.getGroupName());
        for (Channel channel : membersChannel) {
            channel.writeAndFlush(new GroupChatResponseMessage(msg.getFrom(), msg.getContent()));
        }
    }
}
