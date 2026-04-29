package cn.icexmoon.netty.time.client;

import cn.icexmoon.netty.time.client.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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
