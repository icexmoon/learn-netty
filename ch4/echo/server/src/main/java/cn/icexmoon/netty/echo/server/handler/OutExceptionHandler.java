package cn.icexmoon.netty.echo.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName OutExceptionHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/19 17:43
 * @Version 1.0
 */
@Slf4j
public class OutExceptionHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        promise.addListener(future -> {
            if (!future.isSuccess()) {
                log.error(future.cause().getMessage(), future.cause());
                ctx.close();
            }
        });
    }
}
