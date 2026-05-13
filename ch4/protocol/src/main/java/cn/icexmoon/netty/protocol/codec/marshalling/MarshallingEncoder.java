package cn.icexmoon.netty.protocol.codec.marshalling;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * @ClassName MarshallingEncoder
 * @Description 使用 JBoss Marshalling 将对象序列化到 ByteBuf
 * @Author icexmoon@qq.com
 * @Date 2026/5/12 17:16
 * @Version 1.0
 */
@Slf4j
public class MarshallingEncoder {

    /**
     * 将对象序列化并写入 ByteBuf
     *
     * @param object 要序列化的对象
     * @param out    目标 ByteBuf
     * @throws Exception 序列化异常
     */
    public void encode(Object object, ByteBuf out) throws Exception {
        try {
            // 使用 JbossMarshallingUtil 进行序列化
            byte[] data = JbossMarshallingUtil.encode(object);
            
            if (data != null && data.length > 0) {
                // 先写入长度（4字节）
                out.writeInt(data.length);
                // 再写入数据
                out.writeBytes(data);
                
                log.debug("对象序列化成功，类型: {}, 数据长度: {}, ByteBuf 大小: {}",
                        object.getClass().getSimpleName(), data.length, out.readableBytes());
            } else {
                // 空数据，写入长度0
                out.writeInt(0);
                log.debug("对象序列化结果为空，类型: {}", 
                        object != null ? object.getClass().getSimpleName() : "null");
            }
        } catch (IOException e) {
            log.error("对象序列化失败", e);
            throw new IOException("Marshalling 编码失败", e);
        }
    }
}
