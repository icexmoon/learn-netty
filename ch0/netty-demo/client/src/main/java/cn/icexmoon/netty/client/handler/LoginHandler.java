package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.client.service.CommandLineService;
import cn.icexmoon.netty.client.service.UserService;
import cn.icexmoon.netty.util.pojo.ChatRequestMessage;
import cn.icexmoon.netty.util.pojo.LoginRequestMessage;
import cn.icexmoon.netty.util.pojo.LoginResponseMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Scanner;

/**
 * @ClassName LoginHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 10:13
 * @Version 1.0
 */
public class LoginHandler extends ChannelInboundHandlerAdapter {
    private String username;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        UserService.doLogin(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("获取到登录结果");
        if (msg instanceof LoginResponseMessage) {
            LoginResponseMessage loginResponseMessage = (LoginResponseMessage) msg;
            if (loginResponseMessage.isSuccess()) {
                System.out.println("登录成功");
                CommandLineService.readyForCommandInput(ctx);
            } else {
                System.out.printf("登录失败，错误信息：%s%n", msg);
            }
        } else {
            System.out.println("收到未知消息类型：" + msg.getClass().getName());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.err.println("客户端发生异常：" + cause.getMessage());
        cause.printStackTrace();
        ctx.close();
    }
}
