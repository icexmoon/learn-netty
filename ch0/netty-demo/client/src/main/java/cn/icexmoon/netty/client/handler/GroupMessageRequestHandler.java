package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.util.pojo.GroupMessageRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName GroupMessageRequestHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 16:25
 * @Version 1.0
 */
public class GroupMessageRequestHandler extends SimpleChannelInboundHandler<GroupMessageRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupMessageRequest groupMessageRequest) throws Exception {
        System.out.printf("群聊%s收到消息：\n%s\n，发信人：%s\n",
                groupMessageRequest.getGroupName(),
                groupMessageRequest.getContent(),
                groupMessageRequest.getFrom());
    }
}
