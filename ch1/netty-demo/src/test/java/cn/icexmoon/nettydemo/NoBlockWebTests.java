package cn.icexmoon.nettydemo;

import cn.icexmoon.nettydemo.utils.ClientTestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName NoBlockWebTest2
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/21 上午11:20
 * @Version 1.0
 */
@Slf4j
public class NoBlockWeb2Tests {

    private static final int SERVER_PORT = 8080;

    /**
     * 以非阻塞的方式创建一个 NIO 服务端
     */
    @Test
    public void testServer() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(SERVER_PORT));
        // 使用非阻塞模式
        serverSocketChannel.configureBlocking(false);
        // 持有与客户端连接通道的容器
        List<SocketChannel> socketChannels = new ArrayList<>();
        // 用于客户端打印的缓存，因为是单线程，可以复用
        ByteBuffer msgReadBuffer = ByteBuffer.allocate(20);
        while (true) {
            // 尝试建立连接
            SocketChannel socketChannel = serverSocketChannel.accept();
            // 如果获取到连接，返回值是非 null
            if (socketChannel != null) {
                log.info("与客户端建立连接 {}", socketChannel);
                // 使用非阻塞模式
                socketChannel.configureBlocking(false);
                // 添加到连接容器
                socketChannels.add(socketChannel);
            }
            // 遍历连接容器，如果有消息，打印
            for (SocketChannel ss : socketChannels) {
                int read = ss.read(msgReadBuffer);
                // 非阻塞模式下，没客户端没有发送消息，返回 0
                if (read > 0) {
                    msgReadBuffer.flip();
                    String msg = StandardCharsets.UTF_8.decode(msgReadBuffer).toString();
                    log.info("接收到客户端消息:{}, from:{}", msg, ss);
                    // 清空缓冲，等待下一次写入
                    msgReadBuffer.clear();
                }
            }
        }
    }

    @Test
    public void testClient() {
        List<String> messages = List.of("hello", "hi", "你好");
        ClientTestUtil.sendMsgToServerByMultiClients("localhost", SERVER_PORT, messages);
    }
}
