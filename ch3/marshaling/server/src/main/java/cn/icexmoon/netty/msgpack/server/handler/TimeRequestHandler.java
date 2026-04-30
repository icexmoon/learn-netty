package cn.icexmoon.netty.msgpack.server.handler;

import cn.icexmoon.netty.msgpack.util.pojo.TimeRequest;
import cn.icexmoon.netty.msgpack.util.pojo.TimeResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Slf4j
public class TimeRequestHandler extends SimpleChannelInboundHandler<TimeRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TimeRequest msg) throws Exception {
        log.info("收到时间请求{}", msg);
        ZoneId zoneId = msg.getZoneId();
        if (zoneId == null){
            zoneId = ZoneId.of("Asia/Shanghai");
        }
        ZonedDateTime zonedDateTime = ZonedDateTime.now(zoneId);
        TimeResponse timeResponse = new TimeResponse();
        timeResponse.setZonedDateTime(zonedDateTime);
        log.info("准备发送时间响应: {}", timeResponse);
        ctx.writeAndFlush(timeResponse).addListener(future -> {
            if (future.isSuccess()) {
                log.info("时间响应发送成功");
            } else {
                log.error("时间响应发送失败", future.cause());
            }
        });
        log.info("发送时间响应{}", timeResponse);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("TimeRequestHandler.channelRead 接收到消息，类型: {}, 内容: {}", msg.getClass().getName(), msg);
        super.channelRead(ctx, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常", cause);
        ctx.close();
    }
}
