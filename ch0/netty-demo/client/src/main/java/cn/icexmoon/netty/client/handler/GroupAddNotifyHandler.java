package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.util.pojo.GroupAddNotifyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName GroupAddNotifyHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 15:55
 * @Version 1.0
 */
public class GroupAddNotifyHandler extends SimpleChannelInboundHandler<GroupAddNotifyMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupAddNotifyMessage groupAddNotifyMessage) throws Exception {
        System.out.printf("你已加入聊天室[%s]%n", groupAddNotifyMessage.getGroupName());
    }
}
