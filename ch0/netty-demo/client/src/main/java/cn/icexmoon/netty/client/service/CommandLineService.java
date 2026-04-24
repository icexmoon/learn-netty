package cn.icexmoon.netty.client.service;

import cn.icexmoon.netty.util.pojo.*;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Scanner;

/**
 * @ClassName CommandLineService
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 11:29
 * @Version 1.0
 */
@Slf4j
public class CommandLineService {
    private static final Scanner scanner = new Scanner(System.in);

    public static void readyForCommandInput(ChannelHandlerContext ctx) {
        new Thread(() -> {
            while (true) {
                doReadyCommandInput(ctx);
            }
        }, "command-input-thread").start();
    }

    private static void doReadyCommandInput(ChannelHandlerContext ctx) {
        System.out.println("请输入要执行的操作：");
        String line = scanner.nextLine();
        String[] linePartition = line.split("\\$");
        switch (linePartition[0]) {
            case "send":
                ChatRequestMessage chatRequestMessage = new ChatRequestMessage(
                        UserService.getLoginUser(),
                        linePartition[1],
                        linePartition[2]
                );
                log.debug("正在发送消息：{}", chatRequestMessage);
                ctx.writeAndFlush(chatRequestMessage);
                log.debug("消息已发送{}", chatRequestMessage);
                break;
            case "gcreate":
                if (linePartition.length < 3) {
                    System.out.println("请输入群聊名称和群成员，格式为：gcreate 群聊名称 群成员1,群成员2...");
                    return;
                }
                log.debug("正在创建群聊...");
                String groupName = linePartition[1];
                String[] groupMembers = linePartition[2].split(",");
                GroupCreateRequestMessage groupCreateRequestMessage = new GroupCreateRequestMessage(
                        UserService.getLoginUser(),
                        groupName,
                        groupMembers
                );
                ctx.writeAndFlush(groupCreateRequestMessage);
                log.debug("群聊已创建：{}", groupCreateRequestMessage);
                break;
            case "gsend":
                if (linePartition.length < 3) {
                    System.out.println("请输入群聊名称和内容，格式为：gsend$群聊名称$内容");
                    return;
                }
                GroupMessageRequest groupMessageRequest = new GroupMessageRequest(
                        linePartition[1],
                        linePartition[2],
                        UserService.getLoginUser()
                );
                log.debug("正在发送群聊消息：{}", groupMessageRequest);
                ctx.writeAndFlush(groupMessageRequest);
                log.debug("群聊消息已发送：{}", groupMessageRequest);
            case "glist":
                if (linePartition.length < 2) {
                    System.out.println("请输入群聊名称，格式为：glist$群聊名称");
                    return;
                }
                GListRequest gListRequest = new GListRequest(linePartition[1]);
                ctx.writeAndFlush(gListRequest);
                log.debug("群聊列表获取命令已发送：{}", gListRequest);
                break;
            case "gjoin":
                if (linePartition.length < 2) {
                    System.out.println("请输入群聊名称，格式为：gjoin$群聊名称");
                    return;
                }
                GJoinRequest gJoinRequest = new GJoinRequest(
                        linePartition[1], UserService.getLoginUser());
                ctx.writeAndFlush(gJoinRequest);
                log.debug("群聊加入命令已发送：{}", gJoinRequest);
                break;
            case "gquit":
                if (linePartition.length < 2) {
                    System.out.println("请输入群聊名称，格式为：gquit$群聊名称");
                    return;
                }
                GQuitRequest gQuitRequest = new GQuitRequest(
                        linePartition[1], UserService.getLoginUser());
                ctx.writeAndFlush(gQuitRequest);
                log.debug("群聊退出命令已发送：{}", gQuitRequest);
        }
    }

    private static void sendSimpleCommand(ChannelHandlerContext ctx, SimpleCommand simpleCommand) {
        ctx.writeAndFlush(simpleCommand);
        log.debug("简单指令已发送：{}", simpleCommand);
    }
}


