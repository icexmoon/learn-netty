package cn.icexmoon.netty.msgpack.util.codec;

import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import cn.icexmoon.netty.msgpack.util.util.MsgPackUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class MsgPackDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int length = msg.readableBytes();
        log.debug("MsgPackDecoder 接收到 {} 字节的数据", length);
        byte[] array = new byte[length];
        msg.readBytes(array);
        ObjectMessage objectMessage = MsgPackUtil.decode(array, ObjectMessage.class);
        log.debug("MsgPackDecoder 解码成功，className: {}", objectMessage.getClassName());
        out.add(objectMessage);
    }
}
