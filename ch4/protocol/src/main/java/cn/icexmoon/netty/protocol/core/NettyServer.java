package cn.icexmoon.netty.protocol.core;

import cn.icexmoon.netty.protocol.codec.NettyMessageDecoder;
import cn.icexmoon.netty.protocol.codec.NettyMessageEncoder;
import cn.icexmoon.netty.protocol.config.ServerConfig;
import cn.icexmoon.netty.protocol.handler.HeartBeatReqHandler;
import cn.icexmoon.netty.protocol.handler.LoginAuthReqHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName NettyServer
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/13 15:54
 * @Version 1.0
 */
@Slf4j
public class NettyServer {
    private void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel channel) throws Exception {
                            channel.pipeline().addLast(new NettyMessageEncoder());
                            channel.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
                            channel.pipeline().addLast(new LoginAuthReqHandler());
                            channel.pipeline().addLast(new HeartBeatReqHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(ServerConfig.HOST, ServerConfig.PORT).sync();
            log.info("启动 NettyServer 成功");
            channelFuture.channel().closeFuture().sync();
            log.info("NettyServer 停止");
        }
        finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws InterruptedException {
        new NettyServer().start();
    }
}
