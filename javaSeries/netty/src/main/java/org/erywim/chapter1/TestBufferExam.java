package org.erywim.chapter1;

import org.erywim.ByteBufferUtil;

import java.nio.ByteBuffer;

/**
 * @author Erywim 2024/6/10
 */
public class TestBufferExam {
    /*
    网络上有多条数据发送给服务端，数据之间使用 \n 进行分隔
    但由于某种原因这些数据在接收时，被进行了重新组合，例如原始数据有3条为

    * Hello,world
    * I'm zhangsan
    * How are you?

    变成了下面的两个 byteBuffer (黏包，半包)

    * Hello,world`\n`I'm zhangsan`\n`Ho
    * w are you?`\n`

    现在要求你编写程序，将错乱的数据恢复成原始的按 \n 分隔的数据
     */

    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocateDirect(200);
        buffer.put("hello world\nI'm zhangsan\nHo".getBytes());
        split(buffer);
        buffer.put("w are you?\n".getBytes());
        split(buffer);
    }

    private static void split(ByteBuffer buffer) {
        buffer.flip();//切换为读模式
        for (int i = 0; i < buffer.limit(); i++) {
            if (buffer.get(i) == '\n') {//get(i) 不会使得position后移
                int len = i + 1 - buffer.position();
                ByteBuffer temp = ByteBuffer.allocate(len);
                for (int i1 = 0; i1 < len; i1++) {
                    temp.put(buffer.get());//get()会让position后移
                }
                ByteBufferUtil.debugAll(temp);
            }
        }
        buffer.compact();//将没有读取的进行压缩
    }
}
