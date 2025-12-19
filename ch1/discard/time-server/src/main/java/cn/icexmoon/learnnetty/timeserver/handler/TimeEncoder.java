package cn.icexmoon.learnnetty.timeserver.handler;

import cn.icexmoon.learnnetty.utils.pojo.UnixTime;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

/**
 * @ClassName TimeEncoder
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/19 下午2:35
 * @Version 1.0
 */
public class TimeEncoder extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        UnixTime time = (UnixTime) msg;
        ByteBuf byteBuf = ctx.alloc().buffer(4);
        byteBuf.writeInt((int)time.value());
        ctx.write(byteBuf, promise);
    }
}
