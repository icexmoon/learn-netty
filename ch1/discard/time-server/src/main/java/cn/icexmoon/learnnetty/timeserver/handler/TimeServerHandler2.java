package cn.icexmoon.learnnetty.timeserver.handler;

import cn.icexmoon.learnnetty.utils.pojo.UnixTime;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName TimeServerHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/19 上午10:11
 * @Version 1.0
 */
@Slf4j
public class TimeServerHandler2 extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(final ChannelHandlerContext ctx) { // (1)
        log.info("连接已建立");

        UnixTime time = new UnixTime();
        log.info("发送当前时间[{}]给客户端", time);
        final ChannelFuture f = ctx.writeAndFlush(time); // (3)
        f.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                assert f == future;
                log.info("消息已发送完，尝试关闭连接");
                ctx.close();
            }
        }); // (4)
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
