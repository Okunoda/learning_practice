package org.erywim.chapter1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

/**
 * @author Erywim 2024/6/14
 */
public class SelectorClient {
    public static void main(String[] args) throws IOException, InterruptedException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 9090));
        socketChannel.write(Charset.defaultCharset().encode("Hello World"));

        Thread.sleep(20000);
    }
}
