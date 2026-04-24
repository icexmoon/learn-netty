package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.client.service.CommandLineService;
import cn.icexmoon.netty.util.pojo.ChatResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @ClassName ChatMessageResponseHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 10:41
 * @Version 1.0
 */
public class ChatMessageResponseHandler extends SimpleChannelInboundHandler<ChatResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ChatResponseMessage chatResponseMessage) throws Exception {
        if (chatResponseMessage.isSuccess()) {
            System.out.println("消息发送成功");
        } else {
            System.out.println("消息发送失败：" + chatResponseMessage.getReason());
        }
//        CommandLineService.readyForCommandInput(channelHandlerContext);
    }
}
