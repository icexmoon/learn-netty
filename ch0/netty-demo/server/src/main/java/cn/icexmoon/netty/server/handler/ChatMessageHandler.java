package cn.icexmoon.netty.server.handler;

import cn.icexmoon.netty.server.session.SessionFactory;
import cn.icexmoon.netty.util.pojo.ChatRequestMessage;
import cn.icexmoon.netty.util.pojo.ChatResponseMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName ChatMessageHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 10:35
 * @Version 1.0
 */
@Slf4j
public class ChatMessageHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatRequestMessage chatRequestMessage) throws Exception {
        log.debug("接收到聊天消息：{}", chatRequestMessage);
        Channel channel = SessionFactory.getSession().getChannel(chatRequestMessage.getTo());
        if (channel != null) {
            log.debug("正在转发消息...");
            channel.writeAndFlush(chatRequestMessage);
            log.debug("消息已转发：{}", chatRequestMessage);
            ChatResponseMessage chatResponseMessage = new ChatResponseMessage(
                    true,
                    "发送成功"
            );
            sendResponseMsg(channelHandlerContext, chatResponseMessage);
        } else {
            ChatResponseMessage chatResponseMessage = new ChatResponseMessage(
                    false,
                    "对方用户不存在或离线，发送失败"
            );
            sendResponseMsg(channelHandlerContext, chatResponseMessage);
        }
    }

    private static void sendResponseMsg(ChannelHandlerContext channelHandlerContext, ChatResponseMessage chatResponseMessage) {
        log.debug("正在发送消息结果...");
        channelHandlerContext.writeAndFlush(chatResponseMessage);
        log.debug("消息结果已发送：{}", chatResponseMessage);
    }
}
