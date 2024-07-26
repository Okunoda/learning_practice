package org.erywim.chapter3.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.erywim.chapter3.message.LoginRequestMessage;
import org.erywim.chapter3.message.LoginResponseMessage;
import org.erywim.chapter3.server.service.UserService;
import org.erywim.chapter3.server.service.UserServiceFactory;
import org.erywim.chapter3.server.session.Session;
import org.erywim.chapter3.server.session.SessionFactory;

/**
 * @author Erywim 2024/7/26
 */
@ChannelHandler.Sharable
public class LoginRequestMessageHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage msg) throws Exception {
        UserService userService = UserServiceFactory.getUserService();
        boolean login = userService.login(msg.getUsername(), msg.getPassword());
        LoginResponseMessage result;
        if (login) {
            SessionFactory.getSession().bind(ctx.channel(),msg.getUsername());
            result = new LoginResponseMessage(true, "登录成功！");
        } else {
            result = new LoginResponseMessage(false, "用户名或密码错误！");
        }
        ctx.writeAndFlush(result);
    }
}
