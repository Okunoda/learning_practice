package org.erywim.chapter3.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageCodec;
import lombok.extern.slf4j.Slf4j;
import org.erywim.chapter3.config.Config;
import org.erywim.chapter3.message.Message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;

/**
 * @author Erywim 2024/7/23
 */
@ChannelHandler.Sharable//这里是保证消息到了此处进行解析的时候一定是一个完整的包，已经由前面的处理器将包切割成了一个完整的包
@Slf4j
public class MessageCodecSharable extends MessageToMessageCodec<ByteBuf, Message>{
    @Override
    public void encode(ChannelHandlerContext ctx, Message msg, List<Object> outList) throws Exception {
        ByteBuf out = ctx.alloc().buffer();
        // 1. 4 字节的魔数
        out.writeBytes(new byte[]{1, 2, 3, 4});
        // 2. 1 字节的版本,
        out.writeByte(1);
        // 3. 1 字节的序列化方式 jdk 0 , json 1
        out.writeByte(Config.getSerializerAlgorithm().ordinal());//拿到枚举类的顺序值
        // 4. 1 字节的 指令消息类型
        out.writeByte(msg.getMessageType());
        // 5. 4 个字节
        out.writeInt(msg.getSequenceId());
        // 无意义，对齐填充
        out.writeByte(0xff);
        // 6. 获取内容的字节数组
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        ObjectOutputStream oos = new ObjectOutputStream(bos);
//        oos.writeObject(msg);
//        byte[] bytes = bos.toByteArray();
        byte[] bytes = Config.getSerializerAlgorithm().serialize(msg);
        // 7. 长度
        out.writeInt(bytes.length);
        // 8. 写入内容
        out.writeBytes(bytes);
        outList.add(out);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int magicNum = in.readInt();
        byte version = in.readByte();
        byte serializerAlgorithm = in.readByte(); //序列化算法 0 或 1
        byte messageType = in.readByte(); //指令消息类型 0,1,2...
        int sequenceId = in.readInt();
        in.readByte();
        int length = in.readInt();
        byte[] bytes = new byte[length];
        in.readBytes(bytes, 0, length);

//        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bytes));
//        Message message = (Message) ois.readObject();
        //根据消息传来时候的序列化类型找到对应的序列化算法
        Serializer.Algorithm algorithm = Serializer.Algorithm.values()[serializerAlgorithm];
        //确定具体的消息类型
        Class<? extends Message> clazz = Message.getMessageClass(messageType);
        Message message = algorithm.deserialize(bytes, clazz);
        log.info("{}",message);
        out.add(message);
    }
}
