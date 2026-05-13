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
        // lengthFieldOffset=4 (crcCode 后面), lengthFieldLength=4
        // lengthAdjustment=-8: 因为 length 字段表示的是整个消息的总长度（包括 crcCode 和 length 本身）
        //   LengthFieldBasedFrameDecoder 计算的帧长度 = lengthFieldOffset + lengthFieldLength + length + lengthAdjustment
        //   = 4 + 4 + length + (-8) = length，正好是整个消息的长度
        // initialBytesToStrip=0: 不跳过任何字节，保留完整的帧数据
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength, -8, 0);
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
        log.debug("NettyMessageDecoder.decode 被调用, readableBytes: {}", in.readableBytes());
        
        // 调用父类方法处理粘包/拆包，获取完整的帧数据
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        
        if (frame == null) {
            log.debug("父类 decode 返回 null，可能是数据不完整");
            return null;
        }
        
        log.debug("父类 decode 成功，frame readableBytes: {}", frame.readableBytes());
        
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
            
            // 验证长度字段：length 应该等于整个帧的长度
            if (length != frame.readableBytes() + 8) { // +8 是因为已经读取了 crcCode(4) + length(4)
                log.warn("Length field mismatch, expected: {}, actual remaining: {}", 
                        length, frame.readableBytes() + 8);
            } else {
                log.debug("Length field verified: {}", length);
            }
            
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
