package cn.icexmoon.netty.client.handler;

import cn.icexmoon.netty.client.service.CommandLineService;
import cn.icexmoon.netty.client.service.UserService;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName MyClientHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 09:07
 * @Version 1.0
 */
@Slf4j
public class MyClientHandler extends ChannelInboundHandlerAdapter {
    public static final CountDownLatch latch = new CountDownLatch(1);

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 执行登录
        new Thread(() -> {
            UserService.doLogin(ctx);
            log.debug("正在等待登录结果...");
            try {
                latch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.debug("登录结果已收到，准备发送消息...");
            CommandLineService.readyForCommandInput(ctx);
        }).start();
    }
}
