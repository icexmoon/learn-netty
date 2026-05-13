package cn.icexmoon.netty.protocol.handler;

import cn.icexmoon.netty.protocol.pojo.Header;
import cn.icexmoon.netty.protocol.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @ClassName HeartBeatRespHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/13 15:10
 * @Version 1.0
 */
@Slf4j
public class HeartBeatRespHandler extends SimpleChannelInboundHandler<NettyMessage> {
    private ScheduledFuture<?> heatBeatScheduleFuture;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage nettyMessage) throws Exception {
        if (nettyMessage.getHeader() == null) {
            return;
        }
        // 如果是认证响应消息，说明已经通过认证，客户端主动发起心跳请求
        if (nettyMessage.getHeader().getType() == Header.Type.LOGIN_AUTH_RESP) {
            // 使用线程池，每隔5秒发送一次心跳请求
            log.info("开启心跳请求发送定时任务");
            heatBeatScheduleFuture = ctx.executor().scheduleAtFixedRate(() -> {
                ctx.writeAndFlush(buildHeartBeatReq());
            }, 0, 5000, TimeUnit.MILLISECONDS);
        } else if (nettyMessage.getHeader().getType() == Header.Type.HEARTBEAT_RESP) {
            // 如果是心跳响应消息，打印日志
            log.info("收到心跳响应消息{}", nettyMessage);
        } else {
            // 其他消息，继续传递给下一个handler处理
            ctx.fireChannelRead(nettyMessage);
        }
    }

    private static NettyMessage buildHeartBeatReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(Header.Type.HEARTBEAT_REQ);
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (heatBeatScheduleFuture != null){
            heatBeatScheduleFuture.cancel(true);
            heatBeatScheduleFuture = null;
        }
        ctx.fireExceptionCaught(cause);
    }
}
