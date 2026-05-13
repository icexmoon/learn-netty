package cn.icexmoon.netty.protocol.codec;

import cn.icexmoon.netty.protocol.codec.marshalling.MarshallingEncoder;
import cn.icexmoon.netty.protocol.pojo.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @ClassName NettyMessageEncoder
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/12 17:12
 * @Version 1.0
 */
public class NettyMessageEncoder extends MessageToMessageEncoder<NettyMessage> {
    private final MarshallingEncoder marshallingEncoder = new MarshallingEncoder();

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyMessage msg, List<Object> list) throws Exception {
        if (msg == null || msg.getHeader() == null) {
            throw new Exception("The encode message is null");
        }
        ByteBuf sendBuf = Unpooled.buffer();
        sendBuf.writeInt((msg.getHeader().getCrcCode()));
        final int lengthIndex = sendBuf.writerIndex();
        sendBuf.writeInt((msg.getHeader().getLength()));
        sendBuf.writeLong((msg.getHeader().getSessionID()));
        sendBuf.writeByte((msg.getHeader().getType()));
        sendBuf.writeByte((msg.getHeader().getPriority()));
        sendBuf.writeInt((msg.getHeader().getAttachment().size()));
        for (String key : msg.getHeader().getAttachment().keySet()) {
            byte[] keyBytes = key.getBytes(StandardCharsets.UTF_8);
            sendBuf.writeInt(keyBytes.length);
            sendBuf.writeBytes(keyBytes);
            Object value = msg.getHeader().getAttachment().get(key);
            marshallingEncoder.encode(value, sendBuf);
        }
        if (msg.getBody() != null) {
            marshallingEncoder.encode(msg.getBody(), sendBuf);
        } else {
            sendBuf.writeInt(0);
        }
        sendBuf.setInt(lengthIndex, sendBuf.readableBytes());
    }
}
