package cn.icexmoon.learnnetty.simpleserver;

import cn.icexmoon.learnnetty.simpleserver.handler.EchoServerHandler;
import cn.icexmoon.learnnetty.utils.server.SimpleServer;
import cn.icexmoon.learnnetty.utils.util.SimpleServerUtil;
import io.netty.channel.ChannelHandler;
import io.netty.handler.codec.string.StringEncoder;

import java.util.List;

/**
 * Hello world!
 */
public class SimpleServerApp {
    public static void main(String[] args) throws Exception {
//        System.out.println( "Hello World!" );
//        SimpleServer server = new SimpleServer(8080);
//        server.run();
        List<ChannelHandler> handlers = List.of(new StringEncoder(), new EchoServerHandler());
        SimpleServer simpleServer = SimpleServerUtil.createSimpleServer(handlers);
        simpleServer.run();
    }
}
