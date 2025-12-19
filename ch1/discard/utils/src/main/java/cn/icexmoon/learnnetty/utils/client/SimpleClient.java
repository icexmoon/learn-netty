package cn.icexmoon.learnnetty.utils.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SimpleClient
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/19 上午10:52
 * @Version 1.0
 */
@Slf4j
public class SimpleClient {
    // 服务端 ip
    private String host;
    // 服务端端口
    private int port;
    // 客户端处理器链
    private List<ChannelHandler> handlers = new ArrayList<>();

    public SimpleClient(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setHandlers(@NonNull List<ChannelHandler> handlers) {
        this.handlers = handlers;
    }

    public void run(){
        log.info("启动客户端");
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        log.info("配置客户端");
        try {
            Bootstrap b = new Bootstrap(); // (1)
            b.group(workerGroup); // (2)
            b.channel(NioSocketChannel.class); // (3)
            b.option(ChannelOption.SO_KEEPALIVE, true); // (4)
            b.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    for (ChannelHandler handler : handlers) {
                        ch.pipeline().addLast(handler);
                    }
                }
            });

            // Start the client.
            log.info("尝试连接服务端[{}:{}]",host,port);
            ChannelFuture f = b.connect(host, port).sync(); // (5)

            // Wait until the connection is closed.
            f.channel().closeFuture().sync();
            log.info("客户端已关闭");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
