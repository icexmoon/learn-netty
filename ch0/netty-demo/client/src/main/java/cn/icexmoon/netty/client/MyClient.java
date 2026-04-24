package cn.icexmoon.netty.client;

import cn.icexmoon.netty.client.handler.*;
import cn.icexmoon.netty.util.handler.GenericJacksonDecoder;
import cn.icexmoon.netty.util.handler.GenericJacksonEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @ClassName MyClient
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 09:06
 * @Version 1.0
 */
public class MyClient {

    public static void main(String[] args) throws Exception {
        NioEventLoopGroup eventExecutors = new NioEventLoopGroup();
        try {
            //创建bootstrap对象，配置参数
            Bootstrap bootstrap = new Bootstrap();
            //设置线程组
            bootstrap.group(eventExecutors)
                    //设置客户端的通道实现类型
                    .channel(NioSocketChannel.class)
                    //设置连接超时时间
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                    //设置保持活动连接状态
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    //使用匿名内部类初始化通道
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //添加客户端通道的处理器
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast(new GenericJacksonDecoder());
                            pipeline.addLast(new GenericJacksonEncoder());
                            pipeline.addLast(new LoginResponseHandler());
                            pipeline.addLast(new ChatMessageRequestHandler());
                            pipeline.addLast(new ChatMessageResponseHandler());
                            pipeline.addLast(new GroupCreateResponseHandler());
                            pipeline.addLast(new GroupAddNotifyHandler());
                            pipeline.addLast(new GroupMessageRequestHandler());
                            pipeline.addLast(new GroupMessageResponseHandler());
                            pipeline.addLast(new SimpleErrorHandler());
                            pipeline.addLast(new GListResponseHandler());
                            pipeline.addLast(new SImpleSuccessHandler());
                            pipeline.addLast(new MyClientHandler());
                        }
                    });
            System.out.println("聊天室客户端启动...");
            //连接服务端
            ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 6666).sync();
            System.out.println("已连接到服务器：" + channelFuture.channel().remoteAddress());
            //对通道关闭进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.err.println("客户端连接被中断：" + e.getMessage());
            e.printStackTrace();
        } finally {
            //关闭线程组
            eventExecutors.shutdownGracefully();
            System.out.println("客户端已关闭");
        }
    }
}