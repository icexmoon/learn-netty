package cn.icexmoon.nettydemo;

import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

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

    @Test
    public void testReadI(){
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();
        byte c = buffer.get(2);
        System.out.println((char)c);
    }

    @Test
    public void testRewind() {
        // 写入测试数据
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        // 执行读操作
        buffer.flip();
        print(buffer, 4);
        // 重置读指针并从头读取
        buffer.rewind();
        print(buffer, 2);
    }

    private void print(ByteBuffer byteBuffer, int n) {
        int times = n;
        while (times > 0) {
            System.out.println((char)byteBuffer.get());
            times--;
        }
    }

    @Test
    public void testMarkAndReset(){
        // 写入测试数据
        ByteBuffer buffer = ByteBuffer.allocate(10);
        buffer.put(new byte[]{'a', 'b', 'c', 'd'});
        buffer.flip();
        // 读取2个字节
        print(buffer, 2);
        // 标记当前读取位置
        buffer.mark();
        // 继续读取
        print(buffer, 2);
        // 重置指针到mark的位置
        buffer.reset();
        // 读取2个字节
        print(buffer, 2);
    }

    @Test
    public void testStr2ByteBuffer(){
        String str = "abcde";
        ByteBuffer buffer = str2ByteBuffer3(str);
        ByteBufferUtil.debugAll(buffer);
    }

    private static ByteBuffer str2ByteBuffer(String str) {
        byte[] bytes = str.getBytes();
        ByteBuffer buffer = ByteBuffer.allocate(bytes.length);
        buffer.put(bytes);
        buffer.flip();
        return buffer;
    }

    private static ByteBuffer str2ByteBuffer2(String str) {
        byte[] bytes = str.getBytes();
        return ByteBuffer.wrap(bytes);
    }

    private static ByteBuffer str2ByteBuffer3(String str) {
        return StandardCharsets.UTF_8.encode(str);
    }

    @Test
    public void testByteBuffer2Str(){
        String str = "abcde";
        ByteBuffer buffer = str2ByteBuffer3(str);
        ByteBufferUtil.debugAll(buffer);
        String newStr = byteBuffer2Str(buffer);
        System.out.println(newStr);
    }

    private static String byteBuffer2Str(ByteBuffer buffer) {
        return StandardCharsets.UTF_8.decode(buffer).toString();
    }

    @Test
    public void testAllocate(){
        ByteBuffer buffer1 = ByteBuffer.allocate(10);
        ByteBuffer buffer2 = ByteBuffer.allocateDirect(10);
        System.out.println(buffer1.getClass());
        System.out.println(buffer2.getClass());
    }
}
