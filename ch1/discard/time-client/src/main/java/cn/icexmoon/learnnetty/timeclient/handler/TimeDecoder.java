package cn.icexmoon.learnnetty.timeclient.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @ClassName TimeDcodeHandler
 * @Description 用于将4 字节整形时间解码为消息的处理器
 * @Author icexmoon@qq.com
 * @Date 2025/12/19 下午2:13
 * @Version 1.0
 */
public class TimeDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 4) {
            // 如果缓冲区字节数小于4，不处理
            return;
        }
        // 缓冲区字节数大于等于4，作为消息读取，并清空缓冲区
        out.add(in.readBytes(4));
    }
}
