package cn.icexmoon.netty.echo.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName InExceptionHandler
 * @Description 入站消息的异常处理器
 * @Author icexmoon@qq.com
 * @Date 2026/5/19 17:42
 * @Version 1.0
 */
@Slf4j
public class InExceptionHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }
}
