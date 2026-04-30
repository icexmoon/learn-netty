package cn.icexmoon.netty.msgpack.util.codec;

import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import cn.icexmoon.netty.msgpack.util.serializer.ISerializer;
import cn.icexmoon.netty.msgpack.util.util.JbossMarshallingUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @Description 将 Object 转换成 ObjectMessage
 */
@Slf4j
@AllArgsConstructor
public class ObjectToObjectMessageEncoder extends MessageToMessageEncoder<Object> {
    private final ISerializer serializer;
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        log.debug("ObjectEncoder 开始编码，对象类型: {}, 对象: {}", msg.getClass().getName(), msg);
        ObjectMessage message = new ObjectMessage();
        message.setObject(serializer.serialize(msg));
        message.setClassName(msg.getClass().getName());
        log.debug("ObjectEncoder 编码成功，className: {}", message.getClassName());
        out.add(message);
    }
}
