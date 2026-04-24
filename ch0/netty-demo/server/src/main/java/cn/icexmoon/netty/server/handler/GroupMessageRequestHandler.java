package cn.icexmoon.netty.server.handler;

import cn.icexmoon.netty.server.pojo.ChatGroup;
import cn.icexmoon.netty.server.service.ChatGroupService;
import cn.icexmoon.netty.server.session.SessionFactory;
import cn.icexmoon.netty.util.pojo.GroupMessageRequest;
import cn.icexmoon.netty.util.pojo.GroupMessageResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName GroupMessageRequestHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 16:19
 * @Version 1.0
 */
@Slf4j
public class GroupMessageRequestHandler extends SimpleChannelInboundHandler<GroupMessageRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupMessageRequest groupMessageRequest) throws Exception {
        ChatGroup chatGroup = ChatGroupService.getChatGroup(groupMessageRequest.getGroupName());
        if (chatGroup == null) {
            channelHandlerContext.writeAndFlush(GroupMessageResponse.fail("群聊不存在"));
            return;
        }
        chatGroup.getUsers().forEach(user -> {
            Channel channel = SessionFactory.getSession().getChannel(user);
            if (channel != null) {
                channel.writeAndFlush(groupMessageRequest);
                log.debug("群聊消息已转发到{}：{}", user, groupMessageRequest);
            }
        });
        channelHandlerContext.writeAndFlush(GroupMessageResponse.success());
    }
}
