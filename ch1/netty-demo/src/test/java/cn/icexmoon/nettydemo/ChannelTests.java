package cn.icexmoon.nettydemo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

/**
 * @ClassName ChannelTests
 * @Description 展示 Channel 和 ByteBuffer 的基本用法
 * @Author icexmoon@qq.com
 * @Date 2025/10/26 上午10:52
 * @Version 1.0
 */
@Slf4j
public class ChannelTests {
    @Test
    public void testChannel() throws Exception {
        final String filePath = "D:\\workspace\\learn-netty\\ch1\\netty-demo\\src\\test\\resources\\test.txt";
        // 创建一个从文件读取数据的通道
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            FileChannel channel = fileInputStream.getChannel();
            // 创建缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                // 从通道读取数据到缓冲区
                int read = channel.read(buffer);
                if (read == -1) {
                    // 缓冲区中已经没有数据
                    break;
                }
                // 从缓冲区读取内容并打印
                buffer.flip(); // 切换为读模式
                while (buffer.hasRemaining()) {
                    log.info("{}", (char) buffer.get());
                }
                buffer.clear(); // 清空缓冲区(将缓冲区切换回写模式)
            }
        }
    }
}
