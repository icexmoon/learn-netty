package cn.icexmoon.netty.time.server;

import cn.icexmoon.netty.time.server.handler.ChildChannelHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName Server
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/29 15:16
 * @Version 1.0
 */
@Slf4j
public class Server {
    public static void main(String[] args) throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            serverBootstrap.group(bossGroup, workGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            // 使用分隔符解码器解决黏包问题
//                            ByteBuf delimiter = Unpooled.wrappedBuffer("$_".getBytes());
//                            ch.pipeline().addLast(new DelimiterBasedFrameDecoder(1024, delimiter));
                            // 使用换行解码器解决黏包问题
//                            ch.pipeline().addLast(new LineBasedFrameDecoder(1024));
                            // 使用长度解码器解决黏包问题
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4, 0, 4));
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new StringDecoder());
                            ch.pipeline().addLast(new ChildChannelHandler());
                        }
                    });
            log.trace("启动中...");
            ChannelFuture channelFuture = serverBootstrap.bind(8080).sync();
            log.trace("启动成功");
            channelFuture.channel().closeFuture().sync();
            log.trace("服务端已关闭");
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
