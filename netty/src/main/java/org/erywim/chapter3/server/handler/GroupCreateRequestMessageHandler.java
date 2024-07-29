package org.erywim.chapter3.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.erywim.chapter3.message.GroupCreateRequestMessage;
import org.erywim.chapter3.message.GroupCreateResponseMessage;
import org.erywim.chapter3.server.session.Group;
import org.erywim.chapter3.server.session.GroupSession;
import org.erywim.chapter3.server.session.GroupSessionFactory;

import java.util.List;

/**
 * @author Erywim 2024/7/26
 */
@ChannelHandler.Sharable
public class GroupCreateRequestMessageHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage msg) throws Exception {
        //群管理器
        GroupSession groupSession = GroupSessionFactory.getGroupSession();
        Group group = groupSession.createGroup(msg.getGroupName(), msg.getMembers());
        if(group == null) {
            List<Channel> membersChannel = groupSession.getMembersChannel(msg.getGroupName());
            for (Channel channel : membersChannel) {
                channel.writeAndFlush(new GroupCreateResponseMessage(true, "您已被拉入" + msg.getGroupName()));
            }
            ctx.writeAndFlush(new GroupCreateResponseMessage(true, msg.getGroupName() + "创建成功"));
        }else{
            ctx.writeAndFlush(new GroupCreateResponseMessage(false, msg.getGroupName() + "群名已经存在"));
        }
    }
}
