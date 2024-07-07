package org.erywim.chapter1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author Erywim 2024/7/7
 */
public class WriteClient {
    public static void main(String[] args) throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("127.0.0.1", 8888));

        int count =0 ;
        while(true){
            ByteBuffer buffer = ByteBuffer.allocate(1024);
            count += sc.read(buffer);
            System.out.println(count);
            buffer.clear();
        }
    }
}
