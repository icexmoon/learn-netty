package cn.icexmoon.learnnetty.utils.util;

import cn.icexmoon.learnnetty.utils.server.SimpleServer;
import io.netty.channel.ChannelHandler;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * @ClassName DiscardServer
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/18 下午5:32
 * @Version 1.0
 */
@Slf4j
public class SimpleServerUtil {
    public static final int DEFAULT_PORT = 8080;

    /**
     * 创建一个简易的 netty 服务端
     * @param port 监听端口
     * @param handlers 处理器链
     * @return netty 服务端
     */
    public static SimpleServer createSimpleServer(int port,@NonNull List<ChannelHandler> handlers){
        SimpleServer server = new SimpleServer(port);
        server.setChannelHandlers(handlers);
        return server;
    }

    /**
     * 创建一个简易的 netty 服务端
     * @param handlers 处理器链
     * @return netty 服务端
     */
    public static SimpleServer createSimpleServer(@NonNull List<ChannelHandler> handlers){
        return createSimpleServer(DEFAULT_PORT, handlers);
    }
}