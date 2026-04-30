package cn.icexmoon.netty.msgpack.util.codec;

import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import cn.icexmoon.netty.msgpack.util.util.MsgPackUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName ObjectDecoder
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 08:40
 * @Version 1.0
 */
@Slf4j
public class ObjectDecoder extends MessageToMessageDecoder<ObjectMessage> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ObjectMessage msg, List<Object> out) throws Exception {
        String className = msg.getClassName();
        log.debug("ObjectDecoder 开始解码，className: {}", className);
        Class<?> clazz = Class.forName(className);
        Object result = MsgPackUtil.decode(msg.getObject(), clazz);
        log.debug("ObjectDecoder 解码成功，对象: {}", result);
        out.add(result);
    }
}
