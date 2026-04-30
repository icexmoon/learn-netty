package cn.icexmoon.netty.msgpack.util.codec;

import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import cn.icexmoon.netty.msgpack.util.serializer.ISerializer;
import cn.icexmoon.netty.msgpack.util.util.JbossMarshallingUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName ObjectMessageToByteEncoder
 * @Description 将 ObjectMessage 转换成字节数组
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 10:38
 * @Version 1.0
 */
@Slf4j
@AllArgsConstructor
public class ObjectMessageToByteEncoder extends MessageToByteEncoder<ObjectMessage> {
    private final ISerializer serializer;
    @Override
    protected void encode(ChannelHandlerContext ctx, ObjectMessage msg, ByteBuf out) throws Exception {
        log.debug("JbossMarshallingEncoder 开始编码，className: {}", msg.getClassName());
        byte[] bytes = serializer.serialize(msg);
        out.writeBytes(bytes);
        log.debug("JbossMarshallingEncoder 编码成功，写入 {} 字节", bytes.length);
    }
}
