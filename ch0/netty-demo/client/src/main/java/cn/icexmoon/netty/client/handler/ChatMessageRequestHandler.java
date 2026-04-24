package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.util.pojo.ChatRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName ChatMessageRequestHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 10:43
 * @Version 1.0
 */
public class ChatMessageRequestHandler extends SimpleChannelInboundHandler<ChatRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatRequestMessage chatRequestMessage) throws Exception {
        System.out.println("收到新的聊天消息");
        System.out.println("发信人："+chatRequestMessage.getFrom());
        System.out.println("内容："+chatRequestMessage.getContent());
    }
}
