package cn.icexmoon.netty.filerserver;

import cn.icexmoon.netty.filerserver.handler.HttpFileServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * Netty HTTP 文件服务器主类
 * <p>
 * 基于 Netty 框架构建的高性能 HTTP 文件服务器，支持：
 * <ul>
 *     <li>文件下载（分块传输，支持大文件）</li>
 *     <li>目录浏览（HTML 格式的目录列表）</li>
 *     <li>HTTP/1.1 Keep-Alive 连接</li>
 *     <li>异步非阻塞 I/O 模型</li>
 * </ul>
 * <p>
 * 服务器启动流程：
 * <ol>
 *     <li>创建 boss 和 worker 事件循环组</li>
 *     <li>配置 ServerBootstrap 启动器</li>
 *     <li>设置 Channel 处理器链（解码、聚合、编码、分块写入、业务处理）</li>
 *     <li>绑定端口并启动服务器</li>
 *     <li>等待关闭信号并优雅停机</li>
 * </ol>
 *
 * @author icexmoon@qq.com
 * @version 1.0
 * @since 2026/5/12
 */
@Slf4j
public class HttpFileServer {
    /**
     * 服务器启动入口
     * <p>
     * 初始化并启动 HTTP 文件服务器，监听 8080 端口
     *
     * @param args 命令行参数（当前未使用）
     */
    public static void main(String[] args) {
        // 创建 ServerBootstrap 启动器，用于配置和启动服务器
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        
        // 创建 boss 事件循环组：负责接受客户端连接请求
        NioEventLoopGroup bossGroup = new NioEventLoopGroup();
        // 创建 worker 事件循环组：负责处理已建立连接的 I/O 操作
        NioEventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            // 配置服务器启动参数
            serverBootstrap.group(bossGroup, workGroup)
                    // 指定使用 NIO 服务端通道
                    .channel(NioServerSocketChannel.class)
                    // 配置子通道（客户端连接）的处理器链
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            // 获取通道的处理器管道
                            // 按顺序添加以下处理器：
                            
                            // 1. HTTP 请求解码器：将字节流解码为 HttpRequest 对象
                            ch.pipeline().addLast(new HttpRequestDecoder());
                            
                            // 2. HTTP 对象聚合器：将分片的 HTTP 消息聚合为 FullHttpRequest
                            //    参数 65536 表示最大允许的内容长度为 64KB
                            ch.pipeline().addLast(new HttpObjectAggregator(65536));
                            
                            // 3. HTTP 响应编码器：将 HttpResponse 对象编码为字节流
                            ch.pipeline().addLast(new HttpResponseEncoder());
                            
                            // 4. 分块写入处理器：支持大文件的分块传输，避免内存溢出
                            ch.pipeline().addLast(new ChunkedWriteHandler());
                            
                            // 5. 自定义业务处理器：处理文件服务器的核心逻辑
                            ch.pipeline().addLast(new HttpFileServerHandler());
                        }
                    });
            // 设置服务器监听端口
            final int INET_PORT = 8080;
            
            // 绑定端口并同步等待绑定完成
            ChannelFuture channelFuture = serverBootstrap.bind(INET_PORT).sync();
            
            // 记录服务器启动成功日志
            log.info("文件服务器已启动，端口{}...", INET_PORT);
            
            // 同步等待服务器通道关闭（阻塞直到服务器停止）
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            // 处理中断异常（通常是服务器被强制关闭）
            log.error("服务器启动或运行过程中被中断", e);
            Thread.currentThread().interrupt(); // 恢复中断状态
        } finally {
            // 无论是否发生异常，都要优雅地关闭事件循环组
            // shutdownGracefully 会等待所有任务完成后再关闭
            log.info("正在关闭服务器...");
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
            log.info("服务器已关闭");
        }
    }
}
