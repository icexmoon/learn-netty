package cn.icexmoon.netty.time.handler;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName ReadCompletionHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/29 10:51
 * @Version 1.0
 */
@Slf4j
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private final AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel result) {
        this.channel = result;
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.get(body);
        String bodyStr = new String(body, StandardCharsets.UTF_8);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(bodyStr) ?
                String.valueOf(System.currentTimeMillis()) : "BAD ORDER";
        log.info("TimeServer receive: {}", bodyStr);
        log.info("TimeServer send: {}", currentTime);
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        buffer.put(currentTime.getBytes(StandardCharsets.UTF_8));
        buffer.flip();
        channel.write(buffer, buffer, new WriteCompletionHandler(channel));
    }

    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            channel.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static class WriteCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
        private final AsynchronousSocketChannel channel;

        public WriteCompletionHandler(AsynchronousSocketChannel channel) {
            this.channel = channel;
        }

        @Override
        public void completed(Integer result, ByteBuffer attachment) {
            if (attachment.hasRemaining()) {
                channel.write(attachment, attachment, this);
            }
        }

        @Override
        public void failed(Throwable exc, ByteBuffer attachment) {
            try {
                channel.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
