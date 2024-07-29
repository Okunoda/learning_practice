package org.erywim;


import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.logging.LoggingHandler;
import org.erywim.chapter3.config.Config;
import org.erywim.chapter3.message.LoginRequestMessage;
import org.erywim.chapter3.message.Message;
import org.erywim.chapter3.protocol.MessageCodecSharable;
import org.erywim.chapter3.protocol.Serializer;
import org.junit.Test;

/**
 * @author Erywim 2024/7/29
 */
public class TestSerializer {

    @Test
    public void testMethod(){
        MessageCodecSharable messageCodecSharable = new MessageCodecSharable();
        LoggingHandler loggingHandler = new LoggingHandler();
        EmbeddedChannel channel = new EmbeddedChannel(loggingHandler, messageCodecSharable, loggingHandler);

//        channel.writeOutbound(new LoginRequestMessage("zhangsan", "123"));

        ByteBuf byteBuf = messageToByteBuf(new LoginRequestMessage("zhangsan", "123"));
        channel.writeInbound(byteBuf);
    }

    public static ByteBuf messageToByteBuf(Message message){
        int algorithm = Config.getSerializerAlgorithm().ordinal();
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer();
        buffer.writeBytes(new byte[]{1, 2, 3, 4});
        buffer.writeByte(1);
        buffer.writeByte(algorithm);
        buffer.writeByte(message.getMessageType());
        buffer.writeInt(message.getSequenceId());
        buffer.writeByte(0xFF);
        byte[] bytes = Serializer.Algorithm.values()[algorithm].serialize(message);
        buffer.writeInt(bytes.length);
        buffer.writeBytes(bytes);
        return buffer;
    }
}
