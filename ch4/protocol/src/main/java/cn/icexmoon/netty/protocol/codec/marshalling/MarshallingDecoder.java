package cn.icexmoon.netty.protocol.codec.marshalling;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @ClassName MarshallingDecoder
 * @Description 使用 JBoss Marshalling 从 ByteBuf 反序列化对象
 * @Author icexmoon@qq.com
 * @Date 2026/5/12 17:53
 * @Version 1.0
 */
@Slf4j
public class MarshallingDecoder {
    
    /**
     * 从 ByteBuf 反序列化对象
     * 
     * @param in 包含序列化数据的 ByteBuf
     * @return 反序列化后的对象
     * @throws Exception 反序列化异常
     */
    public Object decode(ByteBuf in) throws Exception {
        try {
            // 检查是否有足够的数据读取长度（4字节）
            if (in.readableBytes() < 4) {
                log.warn("ByteBuf 数据不足，无法读取长度信息");
                return null;
            }
            
            // 读取数据长度
            int dataLength = in.readInt();
            
            // 如果长度为0，返回null
            if (dataLength == 0) {
                log.debug("数据长度为0，返回null");
                return null;
            }
            
            // 检查是否有足够的数据
            if (in.readableBytes() < dataLength) {
                log.warn("ByteBuf 数据不足，期望: {}, 实际: {}", dataLength, in.readableBytes());
                return null;
            }
            
            // 读取序列化数据
            byte[] data = new byte[dataLength];
            in.readBytes(data);
            
            // 使用 JbossMarshallingUtil 进行反序列化
            Object obj = JbossMarshallingUtil.decode(data);
            
            log.debug("对象反序列化成功，类型: {}", 
                     obj != null ? obj.getClass().getSimpleName() : "null");
            
            return obj;
            
        } catch (IOException e) {
            log.error("对象反序列化失败", e);
            throw new IOException("Marshalling 解码失败", e);
        } catch (ClassNotFoundException e) {
            log.error("类未找到: {}", e.getMessage());
            throw new ClassNotFoundException("反序列化时类未找到: " + e.getMessage());
        }
    }
}
