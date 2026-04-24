package cn.icexmoon.netty.server.handler;

import cn.icexmoon.netty.server.pojo.ChatGroup;
import cn.icexmoon.netty.server.service.ChatGroupService;
import cn.icexmoon.netty.util.pojo.GListRequest;
import cn.icexmoon.netty.util.pojo.GListResponse;
import cn.icexmoon.netty.util.pojo.SimpleErrorMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName GListRequestHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 17:09
 * @Version 1.0
 */
@Slf4j
public class GListRequestHandler extends SimpleChannelInboundHandler<GListRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GListRequest gListRequest) throws Exception {
        log.debug("收到聊天室列表获取请求：{}", gListRequest);
        String groupName = gListRequest.getGroupName();
        ChatGroup chatGroup = ChatGroupService.getChatGroup(groupName);
        if (chatGroup != null) {
            GListResponse gListResponse = new GListResponse(chatGroup.getUsers());
            channelHandlerContext.writeAndFlush(gListResponse);
            log.debug("发送聊天室列表：{}", gListResponse);
        } else {
            SimpleErrorMessage simpleErrorMessage = new SimpleErrorMessage(String.format("没有聊天室[%s]", groupName));
            channelHandlerContext.writeAndFlush(simpleErrorMessage);
            log.debug("发送错误信息", simpleErrorMessage);
        }
    }
}
