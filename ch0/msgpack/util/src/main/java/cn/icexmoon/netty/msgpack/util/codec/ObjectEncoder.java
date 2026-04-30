package cn.icexmoon.netty.msgpack.util.codec;

import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import cn.icexmoon.netty.msgpack.util.util.MsgPackUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class ObjectEncoder extends MessageToMessageEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) throws Exception {
        log.debug("ObjectEncoder 开始编码，对象类型: {}, 对象: {}", msg.getClass().getName(), msg);
        ObjectMessage message = new ObjectMessage();
        message.setObject(MsgPackUtil.encode(msg));
        message.setClassName(msg.getClass().getName());
        log.debug("ObjectEncoder 编码成功，className: {}", message.getClassName());
        out.add(message);
    }
}
