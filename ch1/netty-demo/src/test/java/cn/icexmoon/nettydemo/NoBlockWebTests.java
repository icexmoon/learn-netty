package cn.icexmoon.nettydemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName NoBlockWebTests
 * @Description 非阻塞式的网络编程测试用例
 * @Author icexmoon@qq.com
 * @Date 2025/11/10 下午3:13
 * @Version 1.0
 */
@Slf4j
public class NoBlockWebTests {
    private static final int PORT = 8080;

    @Test
    public void testServer() throws IOException, InterruptedException {
        // 非阻塞式的服务端
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.configureBlocking(false);
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        // 使用 Selector 实现多路复用
        Selector selector = Selector.open();
        // 为 channel 绑定 accept 事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        while (true) {
            // 获取监听的事件
            int selected = selector.select();
            if (selected == 0) {
                log.info("没有任何事件触发");
                Thread.sleep(1000);
                continue;
            }
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isAcceptable()) {
                    // 有 accept 事件发生，建立连接
                    log.info("有连接请求");
                    // 获取 channel
                    ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                    // 获取连接
                    SocketChannel accept = channel.accept();
                    log.info("已连接：{}", accept);
                    accept.configureBlocking(false);
                    accept.register(selector, SelectionKey.OP_READ);
                    // 处理完事件后，从 selectedKeys 中移除
                    selector.selectedKeys().remove(key);
                } else if (key.isReadable()) {
                    // 有 read 事件发生，读取数据并打印
                    log.info("有数据可读");
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    int read = channel.read(buffer);
                    if (read == -1){
                        // 取消通道注册事件
                        key.cancel();
                        channel.close();
                        log.info("已关闭：{}", channel);
                        continue;
                    }
                    while (read != -1) {
                        buffer.flip();
                        log.info("{}", StandardCharsets.UTF_8.decode(buffer));
                        buffer.clear();
                        read = channel.read(buffer);
                    }
                    selector.selectedKeys().remove(key);
                }
            }
            Thread.sleep(1000);
        }
    }

    @Test
    public void testClient() throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", PORT));
        socketChannel.write(StandardCharsets.UTF_8.encode("hello"));
        socketChannel.close();
    }

    @Test
    public void testClient2() throws IOException {
        try(Socket socket = new Socket("localhost", PORT)){
            System.out.println(socket);
            socket.getOutputStream().write("hello".getBytes());
            System.in.read();
        }
    }
}
