package cn.icexmoon.nettydemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName WebTests
 * @Description 网络编程测试用例
 * @Author icexmoon@qq.com
 * @Date 2025/11/10 上午9:02
 * @Version 1.0
 */
@Slf4j
public class WebTests {
    private static final int PORT = 8080;
    @Test
    public void testHttpServer() throws Exception {
        // 阻塞模式下的服务器端
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 绑定监听端口
        serverSocketChannel.bind(new InetSocketAddress(PORT));
        // 创建用于处理客户端连接的线程池
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        while (true) {
            // 创建一个与客户端的连接
            log.info("等待客户端连接...");
            SocketChannel socketChannel = serverSocketChannel.accept();
            log.info("客户端已连接，{}", socketChannel);
            executorService.execute(() -> {
                try {
                    // 创建一个缓冲区
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    // 读取数据
                    log.info("等待客户端数据...");
                    int read = socketChannel.read(buffer);
                    while (read != -1) {
                        buffer.flip();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);
                        log.info("从客户端收到消息：{}",new String(bytes));
                        buffer.clear();
                        read = socketChannel.read(buffer);
                    }
                    log.info("客户端数据读取完毕");
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Test
    public void testClient() throws Exception {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("localhost", PORT));
        socketChannel.write(StandardCharsets.UTF_8.encode("hello"));
        socketChannel.close();
    }
}
