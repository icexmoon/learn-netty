package cn.icexmoon.netty.msgpack.client;

import cn.icexmoon.netty.msgpack.client.handler.ClientHandler;
import cn.icexmoon.netty.msgpack.client.handler.TimeResponseHandler;
import cn.icexmoon.netty.msgpack.util.codec.*;
import cn.icexmoon.netty.msgpack.util.config.ServerConfig;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

/**
 * @ClassName Client
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 08:48
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
                            ch.pipeline().addLast(new ByteToObjectMessageDecoder(ServerConfig.DEFAULT_SERIALIZER));
                            ch.pipeline().addLast(new ObjectMessageToByteEncoder(ServerConfig.DEFAULT_SERIALIZER));
                            ch.pipeline().addLast(new ObjectMessageToObjectDecoder(ServerConfig.DEFAULT_SERIALIZER));
                            ch.pipeline().addLast(new ObjectToObjectMessageEncoder(ServerConfig.DEFAULT_SERIALIZER));
                            ch.pipeline().addLast(new TimeResponseHandler());
                            ch.pipeline().addLast(new ClientHandler());
                        }
                    });
            ChannelFuture channelFuture = bootstrap.connect(new InetSocketAddress("localhost", ServerConfig.PORT)).sync();
            log.info("启动成功");
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
