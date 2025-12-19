package cn.icexmoon.learnnetty.timeserver.handler;

import cn.icexmoon.learnnetty.utils.pojo.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @ClassName TimeEncoder
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/19 下午2:35
 * @Version 1.0
 */
public class TimeEncoder2 extends MessageToByteEncoder<UnixTime> {
    @Override
    protected void encode(ChannelHandlerContext ctx, UnixTime msg, ByteBuf out) throws Exception {
        out.writeInt((int) msg.value());
    }
}
