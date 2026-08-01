package org.erywim.chapter3.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.DefaultPromise;
import lombok.extern.slf4j.Slf4j;
import org.erywim.chapter3.message.RpcResponseMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Erywim 2024/7/30
 */
@ChannelHandler.Sharable
@Slf4j
public class RpcResponseMessageHandler extends SimpleChannelInboundHandler<RpcResponseMessage> {
    public static final Map<Integer, DefaultPromise<Object>> PROMISES = new ConcurrentHashMap<>();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponseMessage msg) throws Exception {
        log.debug(msg.toString());
        DefaultPromise<Object> promise = PROMISES.remove(msg.getSequenceId());
        if(promise != null ){
            if (msg.getReturnValue() != null) {
                promise.setSuccess(msg.getReturnValue());
            }else{
                promise.setFailure(msg.getExceptionValue());
            }
        }
    }
}
