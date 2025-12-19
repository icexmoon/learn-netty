package cn.icexmoon.learnnetty.timeclient;

import cn.icexmoon.learnnetty.timeclient.handler.TimeClientHandler3;
import cn.icexmoon.learnnetty.timeclient.handler.TimeDecoder2;
import cn.icexmoon.learnnetty.utils.client.SimpleClient;
import cn.icexmoon.learnnetty.utils.util.SimpleClientUtil;
import io.netty.channel.ChannelHandler;
import lombok.NonNull;

import java.util.List;

/**
 * Hello world!
 *
 */
public class TimeClientApp
{
    public static void main( String[] args )
    {
        @NonNull List<ChannelHandler> handlers = List.of(new TimeDecoder2(), new TimeClientHandler3());
        SimpleClient simpleClient = SimpleClientUtil.createSimpleClient(handlers);
        simpleClient.run();
    }
}
