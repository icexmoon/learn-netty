package cn.icexmoon.learnnetty.utils.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName DiscardServer
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/18 下午5:32
 * @Version 1.0
 */
@Slf4j
public class SimpleServer {

    private int port;
    /**
     * netty 的处理器链，会在配置 netty 服务器时添加到 pipline
     */
    private List<ChannelHandler> handlers = new ArrayList<>();

    public SimpleServer(int port) {
        this.port = port;
    }

    /**
     * 将指定处理器添加到 netty 处理器链的尾部
     * @param handler 处理器
     */
    public void addChannelHandler(ChannelHandler handler){
        handlers.add(handler);
    }

    /**
     * 设置 netty 的处理器链
     * @param handlers 处理器链
     */
    public void setChannelHandlers(List<ChannelHandler> handlers){
        this.handlers = handlers;
    }

    public void run() throws Exception {
        log.info("开始启动服务端");
        EventLoopGroup bossGroup = new NioEventLoopGroup(); // (1)
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            log.info("配置服务端并加载处理器");
            ServerBootstrap b = new ServerBootstrap(); // (2)
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class) // (3)
                    .childHandler(new ChannelInitializer<SocketChannel>() { // (4)
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            if (!handlers.isEmpty()){
                                for (ChannelHandler handler : handlers) {
                                    ch.pipeline().addLast(handler);
                                }
                            }
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)          // (5)
                    .childOption(ChannelOption.SO_KEEPALIVE, true); // (6)

            // Bind and start to accept incoming connections.
            log.info("开始监听[{}]端口，并等待建立连接", port);
            ChannelFuture f = b.bind(port).sync(); // (7)

            // Wait until the server socket is closed.
            // In this example, this does not happen, but you can do that to gracefully
            // shut down your server.
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }
}