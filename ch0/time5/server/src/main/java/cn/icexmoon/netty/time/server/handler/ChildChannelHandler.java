package cn.icexmoon.netty.time.server.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName ChildChannelHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/29 15:32
 * @Version 1.0
 */
@Slf4j
public class ChildChannelHandler extends ChannelInboundHandlerAdapter {
    private int counter  = 0;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];
        buf.readBytes(req);
        String body = new String(req, StandardCharsets.UTF_8);
        log.info("The time server receive order:{}", body);
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ?
                new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";
        counter++;
        log.info("The time server send:{}, receive times:{}", currentTime, counter);
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());
        ctx.writeAndFlush(resp);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
        log.error("exceptionCaught", cause);
    }
}
