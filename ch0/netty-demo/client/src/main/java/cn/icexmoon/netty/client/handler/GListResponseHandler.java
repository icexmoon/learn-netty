package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.util.pojo.GListResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName GListResponseHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 17:22
 * @Version 1.0
 */
public class GListResponseHandler extends SimpleChannelInboundHandler<GListResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GListResponse gListResponse) throws Exception {
        System.out.println("聊天室列表：" + gListResponse.getUsers());
    }
}
