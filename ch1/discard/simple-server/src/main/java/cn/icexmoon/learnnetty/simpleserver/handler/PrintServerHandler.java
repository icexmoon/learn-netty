package cn.icexmoon.learnnetty.simpleserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName PrintServerHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/18 下午5:41
 * @Version 1.0
 */
@Slf4j
public class PrintServerHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;
        try {
            String strMsg = in.toString(CharsetUtil.UTF_8);
            log.info("收到消息{}", strMsg);
            System.out.println(strMsg);
            System.out.flush();
        } finally {
            // 释放缓冲（引用对象）
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        // 当异常出现时关闭 channel
        cause.printStackTrace();
        ctx.close();
    }
}
