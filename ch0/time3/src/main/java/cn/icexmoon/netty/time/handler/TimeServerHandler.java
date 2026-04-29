package cn.icexmoon.netty.time.handler;

import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;

import static cn.icexmoon.netty.time.TimeServer.PORT;

/**
 * @ClassName TimeServerHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/28 17:55
 * @Version 1.0
 */
@Slf4j
public class TimeServerHandler implements Runnable {
    private CountDownLatch latch = new CountDownLatch(1);
    private AsynchronousServerSocketChannel serverSocketChannel;

    @Override
    public void run() {
        AsynchronousServerSocketChannel serverSocketChannel = null;
        try {
            serverSocketChannel = AsynchronousServerSocketChannel.open();
            this.serverSocketChannel = serverSocketChannel;
            serverSocketChannel.bind(new InetSocketAddress(PORT));
            log.debug("服务端已经启动，监听端口{}", PORT);
            serverSocketChannel.accept(this, new AcceptCompletionHandler());
            latch.await();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (serverSocketChannel != null) {
                    serverSocketChannel.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static class AcceptCompletionHandler implements CompletionHandler<AsynchronousSocketChannel, TimeServerHandler> {

        @Override
        public void completed(AsynchronousSocketChannel result, TimeServerHandler attachment) {
            attachment.serverSocketChannel.accept(attachment, this);
            log.debug("接收到一个新的连接");
            ByteBuffer byteBuffer = ByteBuffer.allocate(1024);
            result.read(byteBuffer, byteBuffer, new ReadCompletionHandler(result));
        }

        @Override
        public void failed(Throwable exc, TimeServerHandler attachment) {
            try {
                attachment.serverSocketChannel.close();
                attachment.latch.countDown();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
