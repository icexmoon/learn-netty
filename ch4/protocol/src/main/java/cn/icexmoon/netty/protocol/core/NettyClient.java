package cn.icexmoon.netty.protocol.core;

import cn.icexmoon.netty.protocol.codec.NettyMessageDecoder;
import cn.icexmoon.netty.protocol.codec.NettyMessageEncoder;
import cn.icexmoon.netty.protocol.config.ClientConfig;
import cn.icexmoon.netty.protocol.config.ServerConfig;
import cn.icexmoon.netty.protocol.handler.HeartBeatReqHandler;
import cn.icexmoon.netty.protocol.handler.HeartBeatRespHandler;
import cn.icexmoon.netty.protocol.handler.LoginAuthReqHandler;
import cn.icexmoon.netty.protocol.handler.LoginAuthRespHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName NettyClient
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/13 15:35
 * @Version 1.0
 */
@Slf4j
public class NettyClient {
    private ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
    public void connect(String host, int port) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap()
                .group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new NettyMessageEncoder());
                        ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                        ch.pipeline().addLast(new ReadTimeoutHandler(50, TimeUnit.SECONDS));
                        ch.pipeline().addLast(new LoginAuthRespHandler());
                        ch.pipeline().addLast(new HeartBeatRespHandler());
                    }
                });
        try {
            // 不指定本地地址，让操作系统自动分配端口，避免端口占用问题
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("启动 NettyClient 失败", e);
            throw e;
        } finally {
            group.shutdownGracefully();
            // 客户端断开连接后 5 秒后重连
            executor.execute(() -> {
                try {
                    TimeUnit.SECONDS.sleep(5);
                    log.info("尝试重新连接");
                    connect(host, port);
                } catch (Exception e) {
                    log.error("重连失败", e);
                }
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyClient().connect(ServerConfig.HOST, ServerConfig.PORT);
    }
}
