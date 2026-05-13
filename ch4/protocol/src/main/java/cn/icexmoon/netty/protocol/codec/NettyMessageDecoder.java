package cn.icexmoon.netty.protocol.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @ClassName NettyMessageDecoder
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/12 17:52
 * @Version 1.0
 */
public class NettyMessageDecoder extends LengthFieldBasedFrameDecoder {
    public NettyMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
    }


}
