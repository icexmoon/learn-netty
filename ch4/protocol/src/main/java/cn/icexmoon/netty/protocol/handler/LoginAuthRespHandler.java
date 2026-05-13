package cn.icexmoon.netty.protocol.handler;

import cn.icexmoon.netty.protocol.pojo.Header;
import cn.icexmoon.netty.protocol.pojo.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName LoginAuthReqHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/13 10:15
 * @Version 1.0
 */
@Slf4j
public class LoginAuthRespHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(buildLoginAuthReq());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        NettyMessage message = (NettyMessage) msg;
        if (message.getHeader() == null) {
            return;
        }
        if (message.getHeader().getType() == Header.Type.LOGIN_AUTH_RESP) {
            byte result = (byte) message.getBody();
            if (result != (byte) 0) {
                log.info("握手验证失败");
                ctx.close();
            } else {
                log.info("握手验证成功");
                ctx.fireChannelRead(msg);
            }
        } else {
            ctx.fireChannelRead(msg);
        }
    }

    private static NettyMessage buildLoginAuthReq() {
        NettyMessage message = new NettyMessage();
        Header header = new Header();
        header.setType(Header.Type.LOGIN_AUTH_REQ);
        message.setHeader(header);
        return message;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }
}
