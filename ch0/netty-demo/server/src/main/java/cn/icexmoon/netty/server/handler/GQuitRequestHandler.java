package cn.icexmoon.netty.server.handler;

import cn.icexmoon.netty.server.pojo.ChatGroup;
import cn.icexmoon.netty.server.service.ChatGroupService;
import cn.icexmoon.netty.util.pojo.GQuitRequest;
import cn.icexmoon.netty.util.pojo.SImpleSuccessMessage;
import cn.icexmoon.netty.util.pojo.SimpleErrorMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName GQuitRequestHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 17:46
 * @Version 1.0
 */
public class GQuitRequestHandler extends SimpleChannelInboundHandler<GQuitRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GQuitRequest gQuitRequest) throws Exception {
        ChatGroup chatGroup = ChatGroupService.getChatGroup(gQuitRequest.getGroupName());
        if(chatGroup == null){
            channelHandlerContext.writeAndFlush(new SimpleErrorMessage("聊天室不存在"));
            return;
        }
        chatGroup.removeUser(gQuitRequest.getUserName());
        channelHandlerContext.writeAndFlush(new SImpleSuccessMessage("退出聊天室成功"));
    }
}
