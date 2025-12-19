package cn.icexmoon.learnnetty.timeclient.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @ClassName TimeServerHandler
 * @Description 修复分包问题后的处理器类
 * @Author icexmoon@qq.com
 * @Date 2025/12/19 上午10:11
 * @Version 1.0
 */
@Slf4j
public class TimeClientHandler2 extends ChannelInboundHandlerAdapter {
    // 处理器缓冲区
    private ByteBuf byteBuf;

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        // 执行处理器的初始化任务
        // 为处理器初始化一个 4 字节的缓冲区
        byteBuf = ctx.alloc().buffer(4);
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        // 执行处理器的清理任务
        ReferenceCountUtil.release(byteBuf);
        byteBuf = null;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("接收到消息");
        // 将接收到的消息写入缓冲区
        ByteBuf m = (ByteBuf) msg;
        byteBuf.writeBytes(m);
        m.release();
        if (byteBuf.readableBytes() >= 4) {
            // 如果缓冲区中数据够了（4 字节），转换为时间并打印
            long currentTimeMillis = (byteBuf.readUnsignedInt() - 2208988800L) * 1000L;
            System.out.println(new Date(currentTimeMillis));
            ctx.close();
        }
    }
}
