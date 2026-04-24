package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.client.service.CommandLineService;
import cn.icexmoon.netty.client.service.UserService;
import cn.icexmoon.netty.util.pojo.LoginResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName LoginResponseHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 11:11
 * @Version 1.0
 */
@Slf4j
public class LoginResponseHandler extends SimpleChannelInboundHandler<LoginResponseMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginResponseMessage loginResponseMessage) throws Exception {
        log.debug("收到服务器的登陆结果：{}", loginResponseMessage);
        if (loginResponseMessage.isSuccess()) {
            System.out.println("登陆成功");
            UserService.setLoginUser(loginResponseMessage.getUsername());
            MyClientHandler.latch.countDown();
        } else {
            log.info("登陆失败，错误信息：{}", loginResponseMessage.getReason());
            UserService.doLogin(ctx);
        }
    }
}
