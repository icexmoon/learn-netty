package cn.icexmoon.netty.util.handler;

import cn.icexmoon.netty.util.JsonUtil;
import cn.icexmoon.netty.util.pojo.Message;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName GenericJacksonEncoder
 * @Description 通用消息编码器
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 14:20
 * @Version 1.0
 */
public class GenericJacksonEncoder extends MessageToByteEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        Message message = new Message();
        message.setClassName(msg.getClass().getName());
        message.setData(JsonUtil.toJson(msg));

        // 转 JSON 后发送
        String json = JsonUtil.toJson(message);
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);

        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
