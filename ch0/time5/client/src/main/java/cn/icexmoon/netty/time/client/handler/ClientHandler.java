package cn.icexmoon.netty.time.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;

/**
 * @ClassName ClientHandler
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/29 15:41
 * @Version 1.0
 */
@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {
    private int receiveCounter = 0;
    private int sendCounter = 0;

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        // 连接建立后发送时间请求
        for (int i = 0; i < 100; i++) {
            String request = "QUERY TIME ORDER";
            request += System.getProperty("line.separator");
            byte[] bytes = request.getBytes(StandardCharsets.UTF_8);
            ByteBuf buffer = Unpooled.buffer(bytes.length);
            buffer.writeBytes(bytes);
            sendCounter++;
            log.debug("Send times:{}", sendCounter);
            ctx.writeAndFlush(buffer);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        receiveCounter++;
        String body = (String) msg;
        log.info("Now time is:{}, receive times:{}", body, receiveCounter);
    }
}
