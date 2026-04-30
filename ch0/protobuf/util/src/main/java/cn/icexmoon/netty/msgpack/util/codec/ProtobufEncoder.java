package cn.icexmoon.netty.msgpack.util.codec;

import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import cn.icexmoon.netty.msgpack.util.util.MsgPackUtil;
import cn.icexmoon.netty.msgpack.util.util.ProtobufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName ProtobufEncoder
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 10:38
 * @Version 1.0
 */
@Slf4j
public class ProtobufEncoder extends MessageToByteEncoder<ObjectMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ObjectMessage msg, ByteBuf out) throws Exception {
        log.debug("ProtobufEncoder 开始编码，className: {}", msg.getClassName());
        byte[] bytes = ProtobufUtil.encode(msg);
        out.writeBytes(bytes);
        log.debug("ProtobufEncoder 编码成功，写入 {} 字节", bytes.length);
    }
}
