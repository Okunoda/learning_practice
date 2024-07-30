package org.erywim.chapter3.server.handler;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.erywim.chapter3.message.RpcRequestMessage;
import org.erywim.chapter3.message.RpcResponseMessage;
import org.erywim.chapter3.server.service.HelloService;
import org.erywim.chapter3.server.service.ServicesFactory;
import org.erywim.chapter3.server.service.UserServiceFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Erywim 2024/7/30
 */
public class RpcRequestMessageHandler extends SimpleChannelInboundHandler<RpcRequestMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequestMessage msg) throws Exception {
        RpcResponseMessage response = new RpcResponseMessage();
        try {
            HelloService service = (HelloService) ServicesFactory.getService(Class.forName(msg.getInterfaceName()));
            Method method = service.getClass().getMethod(msg.getMethodName(), msg.getParameterTypes());
            Object invoke = method.invoke(service, msg.getParameterValue());

            response.setReturnValue(invoke);
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException |
                 InvocationTargetException e) {
            e.printStackTrace();
            response.setExceptionValue(e);
        }
        ChannelFuture channelFuture = ctx.channel().writeAndFlush(response).addListener(promise ->{
            if(!promise.isSuccess()){
                promise.cause().printStackTrace();
            }
        });
    }
}
