package cn.icexmoon.netty.server.handler;

import cn.icexmoon.netty.server.pojo.ChatGroup;
import cn.icexmoon.netty.server.service.ChatGroupService;
import cn.icexmoon.netty.util.pojo.GJoinRequest;
import cn.icexmoon.netty.util.pojo.SImpleSuccessMessage;
import cn.icexmoon.netty.util.pojo.SimpleErrorMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName GJoinRequestHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 17:32
 * @Version 1.0
 */
public class GJoinRequestHandler extends SimpleChannelInboundHandler<GJoinRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GJoinRequest gJoinRequest) throws Exception {
        String groupName = gJoinRequest.getGroupName();
        String userName = gJoinRequest.getUserName();
        if (!ChatGroupService.groupExist(groupName)) {
            channelHandlerContext.writeAndFlush(new SimpleErrorMessage(String.format("没有聊天室[%s]", groupName)));
            return;
        }
        ChatGroup chatGroup = ChatGroupService.getChatGroup(groupName);
        chatGroup.addUser(userName);
        channelHandlerContext.writeAndFlush(new SImpleSuccessMessage(String.format("已加入聊天室[%s]", groupName)));
    }
}
