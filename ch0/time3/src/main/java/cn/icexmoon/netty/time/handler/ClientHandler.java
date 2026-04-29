package cn.icexmoon.netty.time.handler;

import cn.icexmoon.netty.time.TimeServer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName ClientHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/29 11:16
 * @Version 1.0
 */
@Slf4j
public class ClientHandler implements Runnable, CompletionHandler<Void, ClientHandler> {
    private CountDownLatch latch;
    private AsynchronousSocketChannel channel;
    @Override
    public void run() {
        latch = new CountDownLatch(1);
        AsynchronousSocketChannel channel = null;
        try {
            channel = AsynchronousSocketChannel.open();
            this.channel = channel;
            log.debug("开始连接");
            channel.connect(new InetSocketAddress("localhost", TimeServer.PORT), this, this);
            latch.await();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            latch.countDown();
            try {
                if (channel != null) {
                    channel.close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void completed(Void result, ClientHandler attachment) {
        log.trace("连接成功");
        byte[] message = "QUERY TIME ORDER".getBytes(StandardCharsets.UTF_8);
        ByteBuffer byteBuffer = ByteBuffer.allocate(message.length);
        byteBuffer.put(message);
        byteBuffer.flip();
        channel.write(byteBuffer, byteBuffer, new CompletionHandler<Integer, ByteBuffer>() {

            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                if (attachment.hasRemaining()){
                    channel.write(attachment, attachment, this);
                }
                else{
                    ByteBuffer readBuffer = ByteBuffer.allocate(1024);
                    channel.read(readBuffer, readBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                        @Override
                        public void completed(Integer result, ByteBuffer attachment) {
                            attachment.flip();
                            byte[] bytes = new byte[attachment.remaining()];
                            attachment.get(bytes);
                            String body = new String(bytes, StandardCharsets.UTF_8);
                            log.info("Now time is: {}", body);
                            latch.countDown();
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            log.error("读取数据失败", exc);
                            latch.countDown();
                        }
                    });
                }
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                log.error("写入数据失败", exc);
                try {
                    channel.close();
                    latch.countDown();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @Override
    public void failed(Throwable exc, ClientHandler attachment) {
        try {
            this.channel.close();
            attachment.latch.countDown();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
