package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.util.pojo.GroupCreateResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName GroupCreateResponseHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 15:49
 * @Version 1.0
 */
@Slf4j
public class GroupCreateResponseHandler extends SimpleChannelInboundHandler<GroupCreateResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GroupCreateResponseMessage groupCreateResponseMessage) throws Exception {
        if (groupCreateResponseMessage.isSuccess()) {
            log.debug("群组创建成功");
        } else {
            log.debug("群组创建失败：{}", groupCreateResponseMessage.getReason());
        }
    }
}
