package cn.icexmoon.nettydemo;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;

/**
 * @ClassName ByteBuffer
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/10/26 下午6:25
 * @Version 1.0
 */
public class ByteBufferTests {
    @Test
    public void testByteBuffer() {
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put((byte) 0x61);
        buffer.put((byte) 0x62);
        buffer.put((byte) 0x63);
        ByteBufferUtil.debugAll(buffer);
        buffer.flip();
        ByteBufferUtil.debugAll(buffer);
        System.out.println((char) buffer.get());
        ByteBufferUtil.debugAll(buffer);
        buffer.compact();
        ByteBufferUtil.debugAll(buffer);
        buffer.put(new byte[]{(byte) 0x64, (byte) 0x65});
        ByteBufferUtil.debugAll(buffer);
    }
}
