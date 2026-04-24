package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.util.pojo.SImpleSuccessMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName SImpleSuccessHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 17:29
 * @Version 1.0
 */
public class SImpleSuccessHandler extends SimpleChannelInboundHandler<SImpleSuccessMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SImpleSuccessMessage sImpleSuccessMessage) throws Exception {
        System.out.println(sImpleSuccessMessage.getMessage());
    }
}
