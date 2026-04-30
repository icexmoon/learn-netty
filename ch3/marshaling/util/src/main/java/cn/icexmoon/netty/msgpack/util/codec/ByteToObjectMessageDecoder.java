package cn.icexmoon.netty.msgpack.util.codec;

import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import cn.icexmoon.netty.msgpack.util.serializer.ISerializer;
import cn.icexmoon.netty.msgpack.util.util.JbossMarshallingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName JbossMarshallingDecoder
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 10:38
 * @Version 1.0
 */
@Slf4j
@AllArgsConstructor
public class ByteToObjectMessageDecoder extends MessageToMessageDecoder<ByteBuf> {
    private final ISerializer serializer;
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int length = msg.readableBytes();
        log.debug("JbossMarshallingDecoder 接收到 {} 字节的数据", length);
        byte[] array = new byte[length];
        msg.readBytes(array);
        ObjectMessage objectMessage = serializer.deserialize(array, ObjectMessage.class);
        log.debug("JbossMarshallingDecoder 解码成功，className: {}", objectMessage.getClassName());
        out.add(objectMessage);
    }
}
