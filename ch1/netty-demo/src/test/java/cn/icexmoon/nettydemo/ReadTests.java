package cn.icexmoon.nettydemo;

import org.junit.jupiter.api.Test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;

/**
 * @ClassName ReadTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/11/2 上午11:32
 * @Version 1.0
 */
public class ReadTests {
    @Test
    public void testRead() throws Exception {
        final String filePath = "D:\\workspace\\learn-netty\\ch1\\netty-demo\\src\\test\\resources\\test2.txt";
        // 创建一个从文件读取数据的通道
        try (FileInputStream fileInputStream = new FileInputStream(filePath)) {
            FileChannel channel = fileInputStream.getChannel();
            // 创建缓冲区
            ByteBuffer buffer1 = ByteBuffer.allocate(3);
            ByteBuffer buffer2 = ByteBuffer.allocate(3);
            ByteBuffer buffer3 = ByteBuffer.allocate(5);
            channel.read(new ByteBuffer[]{buffer1, buffer2, buffer3});
            buffer1.flip();
            buffer2.flip();
            buffer3.flip();
            System.out.println(StandardCharsets.UTF_8.decode(buffer1));
            System.out.println(StandardCharsets.UTF_8.decode(buffer2));
            System.out.println(StandardCharsets.UTF_8.decode(buffer3));
        }
    }

    @Test
    public void testWrite() throws IOException{
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("One");
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("Two");
        ByteBuffer buffer3 = StandardCharsets.UTF_8.encode("Three");
        String filePath = "D:\\workspace\\learn-netty\\ch1\\netty-demo\\src\\test\\resources\\test3.txt";
        try(FileOutputStream fileOutputStream = new FileOutputStream(filePath)){
            FileChannel channel = fileOutputStream.getChannel();
            channel.write(new ByteBuffer[]{buffer1, buffer2, buffer3});
        }
    }
}
