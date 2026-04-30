package cn.icexmoon.netty.msgpack.util.codec;

import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import cn.icexmoon.netty.msgpack.util.util.MsgPackUtil;
import cn.icexmoon.netty.msgpack.util.util.ProtobufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName ProtobufDecoder
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 10:38
 * @Version 1.0
 */
@Slf4j
public class ProtobufDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int length = msg.readableBytes();
        log.debug("ProtobufDecoder 接收到 {} 字节的数据", length);
        byte[] array = new byte[length];
        msg.readBytes(array);
        ObjectMessage objectMessage = ProtobufUtil.decode(array, ObjectMessage.class);
        log.debug("ProtobufDecoder 解码成功，className: {}", objectMessage.getClassName());
        out.add(objectMessage);
    }
}
