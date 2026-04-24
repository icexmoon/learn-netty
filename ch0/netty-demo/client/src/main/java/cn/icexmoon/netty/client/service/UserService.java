package cn.icexmoon.netty.client.service;

import cn.icexmoon.netty.util.pojo.LoginRequestMessage;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

/**
 * @ClassName UserService
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 10:59
 * @Version 1.0
 */
public class UserService {
    @Setter
    @Getter
    private static String LoginUser;

    public static void doLogin(ChannelHandlerContext ctx) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("请输入用户名：");
        String username = scanner.nextLine();
        System.out.println("请输入密码：");
        String password = scanner.nextLine();
        LoginRequestMessage msg = new LoginRequestMessage(username, password);
        ctx.writeAndFlush(msg);
        System.out.println("正在登录中...");
    }
}
