package cn.icexmoon.learnnetty.utils.util;

import cn.icexmoon.learnnetty.utils.client.SimpleClient;
import io.netty.channel.ChannelHandler;
import lombok.NonNull;

import java.util.List;

/**
 * @ClassName SimpleClientUtil
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/12/19 上午11:01
 * @Version 1.0
 */
public class SimpleClientUtil {
    /**
     * 创建一个简单 netty 客户端
     * @param host 服务端 ip
     * @param port 服务端端口
     * @param handlers 客户端处理器链
     * @return netty 客户端
     */
    public static SimpleClient createSimpleClient(String host, int port,@NonNull List<ChannelHandler> handlers){
        SimpleClient client = new SimpleClient(host, port);
        client.setHandlers(handlers);
        return client;
    }

    public static SimpleClient createSimpleClient(String host, @NonNull List<ChannelHandler> handlers){
        return createSimpleClient(host, SimpleServerUtil.DEFAULT_PORT, handlers);
    }

    public static SimpleClient createSimpleClient(@NonNull List<ChannelHandler> handlers){
        return createSimpleClient("localhost", handlers);
    }
}
