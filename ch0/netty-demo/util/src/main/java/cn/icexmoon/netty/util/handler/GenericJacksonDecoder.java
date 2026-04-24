package cn.icexmoon.netty.util.handler;

import cn.icexmoon.netty.util.JsonUtil;
import cn.icexmoon.netty.util.pojo.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName GenericJacksonDecoder
 * @Description 对通用消息的解码器
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 14:15
 * @Version 1.0
 */
public class GenericJacksonDecoder extends LengthFieldBasedFrameDecoder {

    public GenericJacksonDecoder() {
        super(1024 * 1024, 0, 4, 0, 4);
    }

    /**
     * 从ByteBuf中解码成POJO
     * @param ctx ChannelHandlerContext
     * @param in ByteBuf
     * @return POJO
     * @throws Exception 抛出异常
     */
    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf buf = (ByteBuf) super.decode(ctx, in);
        if (buf == null)
            return null;

        try {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            String json = new String(bytes, StandardCharsets.UTF_8);

            // 先解析成通用 Message
            Message message = JsonUtil.toBean(json, Message.class);

            // 根据 className 加载类
            Class<?> clazz = Class.forName(message.getClassName());

            // 反序列化为真实 POJO
            return JsonUtil.toBean(message.getData(), clazz);
        } finally {
            buf.release();
        }
    }
}
