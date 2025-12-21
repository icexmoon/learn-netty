package cn.icexmoon.nettydemo.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @ClassName ClientTestUtil
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/21 上午11:54
 * @Version 1.0
 */
@Slf4j
public class ClientTestUtil {
    /**
     * 用多客户端的方式发送消息给服务端以测试 NIO 编程实现的服务端
     * @param host 服务端 ip 地址
     * @param port 服务端端口
     * @param messages 要发给服务端的消息（以独立客户端的方式发送每条消息）
     */
    public static void sendMsgToServerByMultiClients(String host, int port, List<String> messages){
        // 用多线程方式建立客户端并发送消息
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch countDownLatch = new CountDownLatch(messages.size());
        for (String message : messages) {
            executorService.execute(() -> {
                SocketChannel socketChannel = null;
                try {
                    socketChannel = SocketChannel.open(new InetSocketAddress(host, port));
                    Thread.sleep(Duration.ofSeconds(10));
                    log.info("发送消息给服务端：{}", message);
                    socketChannel.write(StandardCharsets.UTF_8.encode(message));
                    // 为了突出客户端并发请求效果，这里阻塞10秒
                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    try {
                        if (socketChannel != null) {
                            socketChannel.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    finally {
                        countDownLatch.countDown();
                    }
                }
            });
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        executorService.shutdown();
    }
}
