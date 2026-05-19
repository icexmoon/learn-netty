package cn.icexmoon.netty.echo.client;

import cn.icexmoon.netty.echo.client.handler.ClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName Client
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/19 09:04
 * @Version 1.0
 */
@Slf4j
public class Client {
    private final int PORT;
    private final String HOST;

    public Client(int port, String host) {
        PORT = port;
        HOST = host;
    }

    public static void main(String[] args) {
        new Client(8080, "127.0.0.1").start();
    }

    private void start() {
        Bootstrap bootstrap = new Bootstrap();
        NioEventLoopGroup group = new NioEventLoopGroup(1);
        try {
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    })
                    .remoteAddress(HOST, PORT);
            ChannelFuture channelFuture = bootstrap.connect().sync();
            log.info("client start at {}", PORT);
            channelFuture.channel().closeFuture().sync();
            log.info("client stop");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }
    }
}
