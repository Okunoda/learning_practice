package org.erywim.chapter2;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.CompositeByteBuf;
import io.netty.util.internal.*;

import static io.netty.buffer.ByteBufUtil.appendPrettyHexDump;
import static io.netty.util.internal.StringUtil.NEWLINE;

/**
 * @author Erywim 2024/7/15
 */
public class ByteBufTest {
    public static void main(String[] args) {
//        helloByteBuf();
//        pooledBuf();
        sliceTest();

    }

    private static void sliceTest() {
        //对ByteBuf进行slice切片的过程中进行的是逻辑切片，并不涉及复制
        ByteBuf buffer = ByteBufAllocator.DEFAULT.buffer(10);
        buffer.writeBytes("abcdefghi".getBytes());
        log(buffer);

        ByteBuf slice1 = buffer.slice(0, 5);
        slice1.retain();//通常获取一个切片之后是需要加1的，以防止对原buf进行释放的时候将slice也影响到了。然后retain + 1之后，slice用完了后再手动调用release释放
        log(slice1);
        ByteBuf slice2 = buffer.slice(5,5);
        log(slice2);
        buffer.setByte(3, 'b');
        buffer.release();//因为上面的切片进行了+1操作，所以这里release是释放不掉的
        log(buffer);
        log(slice1);
//        slice1.release();

        //可组装的ByteBuf
        CompositeByteBuf compositeByteBuf = ByteBufAllocator.DEFAULT.compositeBuffer();
        compositeByteBuf.addComponents(true,slice1,slice2);//这里需要调用写指针自动增长，不然会没办法自动生效去写入数据
//        slice1.release();//components也是使用的零拷贝，所以其组成的ByteBuf不能在component使用完成之前被release
        compositeByteBuf.retain();//建议依旧是写完之后手动 retain 保存一下
        log(compositeByteBuf);

        compositeByteBuf.release();
    }

    private static void pooledBuf() {

        ByteBuf buf1 = ByteBufAllocator.DEFAULT.buffer();
        System.out.println("buf1.getClass() = " + buf1.getClass());
        ByteBuf buf2 = ByteBufAllocator.DEFAULT.heapBuffer();
        System.out.println("buf2.getClass() = " + buf2.getClass());
        ByteBuf buf3 = ByteBufAllocator.DEFAULT.directBuffer();
        System.out.println("buf3.getClass() = " + buf3.getClass());
    }

    private static void helloByteBuf() {
        ByteBuf buf = ByteBufAllocator.DEFAULT.buffer(10);
        log(buf);
        System.out.println("================================================================");

        buf.writeBytes("a".repeat(5).getBytes());
        log(buf);

        StringBuilder sb = new StringBuilder();
        sb.append("a".repeat(11));
        buf.writeBytes(sb.toString().getBytes());
        log(buf);
    }

    private static void log(ByteBuf buffer) {
        int length = buffer.readableBytes();
        int rows = length / 16 + (length % 15 == 0 ? 0 : 1) + 4;
        StringBuilder buf = new StringBuilder(rows * 80 * 2)
                .append("read index:").append(buffer.readerIndex())
                .append(" write index:").append(buffer.writerIndex())
                .append(" capacity:").append(buffer.capacity())
                .append(NEWLINE);
        appendPrettyHexDump(buf, buffer);
        System.out.println(buf.toString());
    }
}
