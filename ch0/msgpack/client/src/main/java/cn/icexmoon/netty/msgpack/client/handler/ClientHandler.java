package cn.icexmoon.netty.msgpack.client.handler;

import cn.icexmoon.netty.msgpack.util.pojo.TimeRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.time.ZoneId;

/**
 * @ClassName ClientHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 09:09
 * @Version 1.0
 */
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 发送时间请求
        TimeRequest timeRequest = new TimeRequest();
        timeRequest.setZoneId(ZoneId.of("UTC"));
        ctx.writeAndFlush(timeRequest);
        log.info("发送时间请求{}", timeRequest);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常", cause);
        ctx.close();
    }
}
