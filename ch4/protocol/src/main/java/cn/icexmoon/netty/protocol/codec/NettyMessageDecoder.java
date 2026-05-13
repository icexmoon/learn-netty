package cn.icexmoon.netty.protocol.codec;

import cn.icexmoon.netty.protocol.codec.marshalling.MarshallingDecoder;
import cn.icexmoon.netty.protocol.pojo.Header;
import cn.icexmoon.netty.protocol.pojo.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName NettyMessageDecoder
 * @Description Netty 消息解码器，将 ByteBuf 解码为 NettyMessage 对象
 * @Author icexmoon@qq.com
 * @Date 2026/5/12 17:52
 * @Version 1.0
 */
@Slf4j
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {
    
    private final MarshallingDecoder marshallingDecoder = new MarshallingDecoder();
    
    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        // lengthFieldOffset=4, lengthFieldLength=4, lengthAdjustment=0, initialBytesToStrip=0
        // initialBytesToStrip=0 表示不跳过任何字节，保留完整的帧数据（包括crcCode和length）
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, 0, 0);
    }

    /**
     * 从ByteBuf中解码出NettyMessage对象
     *
     * @param ctx channel上下文
     * @param in 包含了完整帧数据的ByteBuf
     * @return NettyMessage对象
     * @throws Exception 解码异常
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        // 调用父类方法处理粘包/拆包，获取完整的帧数据
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        
        if (frame == null) {
            return null;
        }
        
        try {
            NettyMessage message = new NettyMessage();
            Header header = new Header();
            
            // 1. 读取 crcCode (4字节)
            int crcCode = frame.readInt();
            if (crcCode != header.getCrcCode()) {
                log.error("CRC code mismatch, expected: {}, actual: {}", header.getCrcCode(), crcCode);
                throw new IllegalArgumentException("Invalid CRC code: " + crcCode);
            }
            
            // 2. 读取 length (4字节)
            int length = frame.readInt();
            header.setLength(length);
            
            // 3. 读取 sessionID (8字节)
            long sessionID = frame.readLong();
            header.setSessionID(sessionID);
            
            // 4. 读取 type (1字节)
            byte type = frame.readByte();
            header.setType(type);
            
            // 5. 读取 priority (1字节)
            byte priority = frame.readByte();
            header.setPriority(priority);
            
            // 6. 读取 attachment 大小 (4字节)
            int attachmentSize = frame.readInt();
            Map<String, Object> attachment = new HashMap<>();
            
            // 7. 读取 attachment 内容
            for (int i = 0; i < attachmentSize; i++) {
                // 读取 key 的长度
                int keyLength = frame.readInt();
                // 读取 key 的字节数据
                byte[] keyBytes = new byte[keyLength];
                frame.readBytes(keyBytes);
                String key = new String(keyBytes, StandardCharsets.UTF_8);
                
                // 读取 value（使用 MarshallingDecoder）
                Object value = marshallingDecoder.decode(frame);
                attachment.put(key, value);
            }
            header.setAttachment(attachment);
            
            // 8. 读取 body
            Object body = null;
            // 检查是否还有数据可读（body 可能存在）
            if (frame.readableBytes() > 0) {
                body = marshallingDecoder.decode(frame);
            }
            
            // 设置 header 和 body
            message.setHeader(header);
            message.setBody(body);
            
            log.debug("解码成功 - SessionID: {}, Type: {}, Priority: {}, AttachmentSize: {}, HasBody: {}",
                    sessionID, type, priority, attachmentSize, body != null);
            
            return message;
            
        } catch (Exception e) {
            log.error("解码 NettyMessage 失败", e);
            throw e;
        } finally {
            // 释放 ByteBuf
            if (frame != null && frame.refCnt() > 0) {
                frame.release();
            }
        }
    }
}
