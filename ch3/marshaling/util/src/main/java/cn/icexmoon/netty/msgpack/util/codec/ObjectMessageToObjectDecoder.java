package cn.icexmoon.netty.msgpack.util.codec;

import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import cn.icexmoon.netty.msgpack.util.serializer.ISerializer;
import cn.icexmoon.netty.msgpack.util.util.JbossMarshallingUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName ObjectDecoder
 * @Description 从 ObjectMessage 中解码出 Object
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 08:40
 * @Version 1.0
 */
@Slf4j
@AllArgsConstructor
public class ObjectMessageToObjectDecoder extends MessageToMessageDecoder<ObjectMessage> {
    private final ISerializer serializer;
    @Override
    protected void decode(ChannelHandlerContext ctx, ObjectMessage msg, List<Object> out) throws Exception {
        String className = msg.getClassName();
        log.debug("ObjectDecoder 开始解码，className: {}", className);
        Class<?> clazz = Class.forName(className);
        // 使用 JbossMarshallingUtil 而不是 ProtobufUtil
        Object result = serializer.deserialize(msg.getObject(), clazz);
        log.debug("ObjectDecoder 解码成功，对象: {}", result);
        out.add(result);
    }
}
