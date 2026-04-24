package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.util.pojo.GroupMessageResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName GroupMessageResponseHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 16:29
 * @Version 1.0
 */
@Slf4j
public class GroupMessageResponseHandler extends SimpleChannelInboundHandler<GroupMessageResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, GroupMessageResponse groupMessageResponse) throws Exception {
        log.debug("收到群消息响应：{}", groupMessageResponse);
        if (!groupMessageResponse.isSuccess()) {
            log.debug("群消息发送失败：{}", groupMessageResponse.getReason());
        }
        else{
            log.debug("群消息发送成功");
        }
    }
}
