package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.util.pojo.SimpleErrorMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName SimpleErrorHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 17:13
 * @Version 1.0
 */
public class SimpleErrorHandler extends SimpleChannelInboundHandler<SimpleErrorMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SimpleErrorMessage simpleErrorMessage) throws Exception {
        System.out.println("服务器返回错误：" + simpleErrorMessage.getError());
    }
}
