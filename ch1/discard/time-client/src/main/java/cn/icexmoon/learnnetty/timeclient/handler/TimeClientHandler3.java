package cn.icexmoon.learnnetty.timeclient.handler;

import cn.icexmoon.learnnetty.utils.pojo.UnixTime;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName TimeClientHandler
 * @Description 使用 POJO 作为时间消息的类型
 * @Author icexmoon@qq.com
 * @Date 2025/12/19 上午11:08
 * @Version 1.0
 */
@Slf4j
public class TimeClientHandler3 extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("接收到服务端消息");
        UnixTime currentTime = (UnixTime) msg;
        System.out.println(currentTime);
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
