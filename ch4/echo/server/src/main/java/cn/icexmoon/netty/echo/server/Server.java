package cn.icexmoon.netty.echo.server;

import cn.icexmoon.netty.echo.server.handler.ServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName Server
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/19 09:03
 * @Version 1.0
 */
@Slf4j
public class Server {
    private final int PORT;

    public Server(int port) {
        PORT = port;
    }

    private void start() {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        final ServerHandler serverHandler = new ServerHandler();
        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast(serverHandler);
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(PORT).sync();
            log.info("server start at {}", PORT);
            channelFuture.channel().closeFuture().sync();
            log.info("server stop");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new Server(8080).start();
    }
}
