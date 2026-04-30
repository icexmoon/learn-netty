package cn.icexmoon.netty.msgpack.client.handler;

import cn.icexmoon.netty.msgpack.util.pojo.TimeResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TimeResponseHandler extends SimpleChannelInboundHandler<TimeResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TimeResponse msg) throws Exception {
        log.info("收到时间响应：{}", msg);
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("TimeResponseHandler.channelRead 接收到消息，类型: {}, 内容: {}", msg.getClass().getName(), msg);
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常", cause);
        ctx.close();
    }
}
