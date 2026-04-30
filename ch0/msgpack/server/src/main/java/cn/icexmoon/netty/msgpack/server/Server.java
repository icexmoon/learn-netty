package cn.icexmoon.netty.msgpack.server;

import cn.icexmoon.netty.msgpack.server.handler.TimeRequestHandler;
import cn.icexmoon.netty.msgpack.util.codec.MsgPackDecoder;
import cn.icexmoon.netty.msgpack.util.codec.MsgPackEncoder;
import cn.icexmoon.netty.msgpack.util.codec.ObjectDecoder;
import cn.icexmoon.netty.msgpack.util.codec.ObjectEncoder;
import cn.icexmoon.netty.msgpack.util.config.ServerConfig;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

/**
 * @ClassName Server
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 08:48
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
                    .childHandler(new ChannelInitializer<NioSocketChannel>(){
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            // 添加异常处理器
                            ch.pipeline().addLast(new io.netty.channel.ChannelInboundHandlerAdapter() {
                                @Override
                                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                                    log.error("Pipeline 异常", cause);
                                    cause.printStackTrace();
                                }
                            });
                            ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(65535, 0, 4, 0, 4));
                            ch.pipeline().addLast(new LengthFieldPrepender(4));
                            ch.pipeline().addLast(new MsgPackDecoder());
                            ch.pipeline().addLast(new MsgPackEncoder());
                            ch.pipeline().addLast(new ObjectDecoder());
                            ch.pipeline().addLast(new ObjectEncoder());
                            ch.pipeline().addLast(new TimeRequestHandler());
                        }
                    });
            ChannelFuture channelFuture = serverBootstrap.bind(ServerConfig.PORT).sync();
            log.info("启动成功");
            channelFuture.channel().closeFuture().sync();
            log.info("关闭成功");
        } finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
}
