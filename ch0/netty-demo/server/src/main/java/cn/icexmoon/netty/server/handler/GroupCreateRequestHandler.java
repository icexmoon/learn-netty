package cn.icexmoon.netty.server.handler;

import cn.icexmoon.netty.server.pojo.ChatGroup;
import cn.icexmoon.netty.server.service.ChatGroupService;
import cn.icexmoon.netty.server.session.SessionFactory;
import cn.icexmoon.netty.util.pojo.GroupAddNotifyMessage;
import cn.icexmoon.netty.util.pojo.GroupCreateRequestMessage;
import cn.icexmoon.netty.util.pojo.GroupCreateResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName GroupCreateMessageHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 15:28
 * @Version 1.0
 */
@Slf4j
public class GroupCreateRequestHandler extends SimpleChannelInboundHandler<GroupCreateRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateRequestMessage groupCreateRequestMessage) throws Exception {
        if (ChatGroupService.groupExist(groupCreateRequestMessage.getGroupName())) {
            ctx.writeAndFlush(GroupCreateResponseMessage.fail(String.format("群组[%s]已存在", groupCreateRequestMessage.getGroupName())));
            return;
        }
        Set<String> names = new HashSet<>(Arrays.asList(groupCreateRequestMessage.getMembers()));
        names.add(groupCreateRequestMessage.getCreator());
        ChatGroup chatGroup = new ChatGroup(groupCreateRequestMessage.getGroupName(), names);
        ChatGroupService.addChatGroup(chatGroup);
        log.debug("群组已创建：{}", chatGroup);
        GroupCreateResponseMessage groupCreateResponseMessage = GroupCreateResponseMessage.success();
        ctx.writeAndFlush(groupCreateResponseMessage);
        log.debug("群组创建响应报文已发送：{}", groupCreateResponseMessage);
        chatGroup.getUsers().forEach(username -> {
            Channel channel = SessionFactory.getSession().getChannel(username);
            if (channel != null) {
                channel.writeAndFlush(new GroupAddNotifyMessage(chatGroup.getName()));
            }
        });
    }
}
