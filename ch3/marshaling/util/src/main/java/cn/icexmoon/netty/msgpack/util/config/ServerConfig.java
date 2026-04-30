package cn.icexmoon.netty.msgpack.util.config;

import cn.icexmoon.netty.msgpack.util.serializer.ISerializer;
import cn.icexmoon.netty.msgpack.util.serializer.impl.JbossMarshallingSerializer;
import cn.icexmoon.netty.msgpack.util.serializer.impl.MsgPackSerializer;
import cn.icexmoon.netty.msgpack.util.serializer.impl.ProtobufSerializer;

/**
 * @ClassName ServerConfig
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 08:58
 * @Version 1.0
 */
public class ServerConfig {
    public static final int PORT = 8080;
    public static final ISerializer DEFAULT_SERIALIZER = new ProtobufSerializer();
}
