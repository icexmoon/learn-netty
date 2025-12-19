package cn.icexmoon.learnnetty.simpleserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName EchoServerHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/19 上午8:27
 * @Version 1.0
 */
@Slf4j
public class EchoServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        String strMsg = ((ByteBuf) msg).toString(StandardCharsets.UTF_8);
        log.info("收到消息{}", strMsg);
        ctx.writeAndFlush(strMsg);
        log.info("返回消息{}", strMsg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
