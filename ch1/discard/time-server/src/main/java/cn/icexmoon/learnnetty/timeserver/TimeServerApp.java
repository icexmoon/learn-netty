package cn.icexmoon.learnnetty.timeserver;

import cn.icexmoon.learnnetty.timeserver.handler.TimeEncoder2;
import cn.icexmoon.learnnetty.timeserver.handler.TimeServerHandler2;
import cn.icexmoon.learnnetty.utils.server.SimpleServer;
import cn.icexmoon.learnnetty.utils.util.SimpleServerUtil;
import io.netty.channel.ChannelHandler;
import lombok.NonNull;

import java.util.List;

/**
 * Hello world!
 *
 */
public class TimeServerApp
{
    public static void main( String[] args ) throws Exception {
        @NonNull List<ChannelHandler> handlers = List.of(new TimeEncoder2(),new TimeServerHandler2());
        SimpleServer simpleServer = SimpleServerUtil.createSimpleServer(handlers);
        simpleServer.run();
    }
}
