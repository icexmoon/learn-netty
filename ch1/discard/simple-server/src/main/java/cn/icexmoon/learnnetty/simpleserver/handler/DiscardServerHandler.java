package cn.icexmoon.learnnetty.simpleserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @ClassName DiscardServerHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/18 下午5:28
 * @Version 1.0
 */
public class DiscardServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) { // (2)
        // 丢弃接收到的消息
        ((ByteBuf) msg).release(); // (3)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) { // (4)
        // 当异常出现时关闭 channel
        cause.printStackTrace();
        ctx.close();
    }

}
