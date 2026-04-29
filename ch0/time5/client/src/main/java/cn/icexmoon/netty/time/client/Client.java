package cn.icexmoon.netty.time.client;

import cn.icexmoon.netty.time.client.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @ClassName Client
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/29 15:17
 * @Version 1.0
 */
@Slf4j
public class Client {
    public static void main(String[] args) throws InterruptedException {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            // 使用分隔符解码器解决黏包问题
                            ByteBuf delimiter = Unpooled.wrappedBuffer("$_".getBytes());
                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture connect = bootstrap.connect(new InetSocketAddress("localhost", 8080));
            log.trace("正在连接服务端...");
            connect.sync();
            log.trace("已连接到服务端{}", connect.channel());
            connect.channel().closeFuture().sync();
            log.trace("已断开连接");
        } finally {
            group.shutdownGracefully();
        }
    }
}
