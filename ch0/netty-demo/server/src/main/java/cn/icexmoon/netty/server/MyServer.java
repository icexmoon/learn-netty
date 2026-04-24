package cn.icexmoon.netty.server;

import cn.icexmoon.netty.server.handler.*;
import cn.icexmoon.netty.util.handler.GenericJacksonDecoder;
import cn.icexmoon.netty.util.handler.GenericJacksonEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @ClassName MyServer
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 09:03
 * @Version 1.0
 */
public class MyServer {
    public static void main(String[] args) throws Exception {
        //创建两个线程组 boosGroup、workerGroup
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            //创建服务端的启动对象，设置参数
            ServerBootstrap bootstrap = new ServerBootstrap();
            //设置两个线程组boosGroup和workerGroup
            bootstrap.group(bossGroup, workerGroup)
                    //设置服务端通道实现类型
                    .channel(NioServerSocketChannel.class)
                    //设置线程队列得到连接个数
                    .option(ChannelOption.SO_BACKLOG, 128)
                    //设置保持活动连接状态
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    //使用匿名内部类的形式初始化通道对象
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            //给pipeline管道设置处理器
                            ChannelPipeline pipeline = socketChannel.pipeline();
                            pipeline.addLast(new GenericJacksonDecoder());
                            pipeline.addLast(new GenericJacksonEncoder());
                            pipeline.addLast(new LoginHandler());
                            pipeline.addLast(new ChatMessageHandler());
                            pipeline.addLast(new GroupCreateRequestHandler());
                            pipeline.addLast(new GroupMessageRequestHandler());
                            pipeline.addLast(new GListRequestHandler());
                            pipeline.addLast(new GJoinRequestHandler());
                            pipeline.addLast(new SimpleCommandHandler());
                            pipeline.addLast(new GQuitRequestHandler());
                        }
                    });//给workerGroup的EventLoop对应的管道设置处理器
            System.out.println("聊天室服务端已经准备就绪...");
            //绑定端口号，启动服务端
            ChannelFuture channelFuture = bootstrap.bind(6666).sync();
            System.out.println("服务器已启动，监听端口：6666");
            
            // 添加服务器关闭钩子
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("正在关闭服务器...");
                bossGroup.shutdownGracefully();
                workerGroup.shutdownGracefully();
            }));
            
            //对关闭通道进行监听
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            System.err.println("服务器被中断：" + e.getMessage());
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            System.out.println("服务器已完全关闭");
        }
    }
}
