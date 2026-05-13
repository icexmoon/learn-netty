package cn.icexmoon.netty.protocol.handler;

import cn.icexmoon.netty.protocol.pojo.Header;
import cn.icexmoon.netty.protocol.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName HeartBeatReqHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/13 15:19
 * @Version 1.0
 */
@Slf4j
public class HeartBeatReqHandler extends SimpleChannelInboundHandler<NettyMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyMessage nettyMessage) throws Exception {
        if (nettyMessage.getHeader() == null) {
            return;
        }
        // 如果是心跳请求消息，返回心跳响应消息
        if (nettyMessage.getHeader().getType() == Header.Type.HEARTBEAT_REQ) {
            log.trace("收到心跳请求消息{}", nettyMessage);
            NettyMessage respMsg = buildHeatBeatResp();
            ctx.writeAndFlush(respMsg);
            log.trace("发送心跳响应消息{}", respMsg);
        } else {
            // 其他消息，继续转发
            ctx.fireChannelRead(nettyMessage);
        }
    }

    private static NettyMessage buildHeatBeatResp() {
        NettyMessage respMsg = new NettyMessage();
        Header header = new Header();
        header.setType(Header.Type.HEARTBEAT_RESP);
        respMsg.setHeader(header);
        return respMsg;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
