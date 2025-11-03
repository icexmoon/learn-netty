package cn.icexmoon.nettydemo;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName StickPackageTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/11/3 上午8:39
 * @Version 1.0
 */
public class StickPackageTests {
    @Test
    public void test() {
        // 模拟接受到黏包和半包数据
        ByteBuffer buffer1 = StandardCharsets.UTF_8.encode("Hello world\nHow are you\n你");
        ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("好！\n");
        // 处理黏包和半包数据
        List<ByteBuffer> buffers = dealStickPackage(List.of(buffer1, buffer2));
        for (ByteBuffer buffer : buffers) {
            System.out.println(StandardCharsets.UTF_8.decode(buffer));
        }
    }

    private static List<ByteBuffer> dealStickPackage(List<ByteBuffer> sourceBuffers) {
        List<ByteBuffer> targetBuffers = new ArrayList<ByteBuffer>();
        for (ByteBuffer buffer : sourceBuffers) {
            dealStickPackage(targetBuffers, buffer);
        }
        return targetBuffers;
    }

    private static void dealStickPackage(List<ByteBuffer> targetBuffers, ByteBuffer sourceBuffer) {
        // 遍历 sourceBuffer，用分隔符进行拆分
        if (sourceBuffer.limit() == 0) {
            return;
        }
        int start = 0;
        for (int i = 0; i < sourceBuffer.limit(); i++) {
            byte b = sourceBuffer.get(i);
            if (b == '\n' || i == sourceBuffer.limit() - 1) {
                // 找到分隔符，创建一个新buffer，将数据写入新buffer中
                int capacity = i - start + 1;
                if (capacity <= 0) {
                    break;
                }
                ByteBuffer buffer = ByteBuffer.allocate(capacity);
                buffer.put(sourceBuffer.array(), start, capacity);
                buffer.flip();
                // 如果已处理集合中最后一个 buffer 结尾不是 \n，则将两个 buffer 合并
                if (!targetBuffers.isEmpty() && targetBuffers.getLast().get(targetBuffers.getLast().limit() - 1) != '\n') {
                    ByteBuffer lastBuffer = targetBuffers.getLast();
                    int lastCapacity = lastBuffer.limit() + buffer.limit();
                    ByteBuffer newBuffer = ByteBuffer.allocate(lastCapacity);
                    newBuffer.put(lastBuffer);
                    newBuffer.put(buffer);
                    targetBuffers.removeLast();
                    newBuffer.flip();
                    targetBuffers.add(newBuffer);
                } else {
                    targetBuffers.add(buffer);
                }
                start = i + 1;
            }
        }
    }
}
