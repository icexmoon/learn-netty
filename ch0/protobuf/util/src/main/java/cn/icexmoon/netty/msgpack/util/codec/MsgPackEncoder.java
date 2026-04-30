package cn.icexmoon.netty.msgpack.util.codec;

import cn.icexmoon.netty.msgpack.util.util.MsgPackUtil;
import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MsgPackEncoder extends MessageToByteEncoder<ObjectMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, ObjectMessage msg, ByteBuf out) throws Exception {
        log.debug("MsgPackEncoder 开始编码，className: {}", msg.getClassName());
        byte[] bytes = MsgPackUtil.encode(msg);
        out.writeBytes(bytes);
        log.debug("MsgPackEncoder 编码成功，写入 {} 字节", bytes.length);
    }
}
