package cn.icexmoon.netty.server.handler;

import cn.icexmoon.netty.server.service.ChatGroupService;
import cn.icexmoon.netty.util.pojo.GListResponse;
import cn.icexmoon.netty.util.pojo.SimpleCommand;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName SimpleCommandHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 16:50
 * @Version 1.0
 */
public class SimpleCommandHandler extends SimpleChannelInboundHandler<SimpleCommand> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, SimpleCommand simpleCommand) throws Exception {
        switch (simpleCommand.getCommand()){
            case SimpleCommand.GLIST:
                ChatGroupService.getChatGroup(simpleCommand.getFrom());
                new GListResponse();
                break;
        }
    }
}
