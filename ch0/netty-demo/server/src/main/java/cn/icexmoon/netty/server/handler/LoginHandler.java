package cn.icexmoon.netty.server.handler;

import cn.icexmoon.netty.server.service.UserService;
import cn.icexmoon.netty.server.session.SessionFactory;
import cn.icexmoon.netty.util.pojo.LoginRequestMessage;
import cn.icexmoon.netty.util.pojo.LoginResponseMessage;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


/**
 * @ClassName LoginHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 10:27
 * @Version 1.0
 */
@Slf4j
@ChannelHandler.Sharable
public class LoginHandler extends SimpleChannelInboundHandler<LoginRequestMessage> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, LoginRequestMessage loginRequestMessage) throws Exception {
        try {
            System.out.println("正在处理登陆请求...");
            String username = loginRequestMessage.getUsername();
            String password = loginRequestMessage.getPassword();
            System.out.printf("正在验证用户信息(userName: %s,password: %s)%n", username, password);
            boolean checked = UserService.checkPassword(username, password);
            LoginResponseMessage message;
            if (checked) {
                message = LoginResponseMessage.success(username);
                SessionFactory.getSession().bind(ctx.channel(), username);
                System.out.println("用户" + username + "登录成功");
            } else {
                message = LoginResponseMessage.fail("用户名或密码错误");
            }
            log.debug("正在发送登陆结果：{}", message);
            ctx.writeAndFlush(message).addListener(future -> {
                if (!future.isSuccess()) {
                    System.err.println("发送登录响应失败：" + future.cause().getMessage());
                }
            });
        } catch (Exception e) {
            System.err.println("处理登录请求时发生异常：" + e.getMessage());
            e.printStackTrace();
            LoginResponseMessage errorMessage = LoginResponseMessage.fail("服务器内部错误");
            ctx.writeAndFlush(errorMessage);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("服务端发生异常：" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
