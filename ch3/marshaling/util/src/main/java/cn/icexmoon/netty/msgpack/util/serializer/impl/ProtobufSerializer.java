package cn.icexmoon.netty.msgpack.util.serializer.impl;

import cn.icexmoon.netty.msgpack.util.serializer.ISerializer;
import cn.icexmoon.netty.msgpack.util.util.ProtobufUtil;

/**
 * @ClassName ProtobufSerializer
 * @Description 使用 Protobuf 4.34 进行序列化
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 14:13
 * @Version 1.0
 */
public class ProtobufSerializer implements ISerializer {
    @Override
    public byte[] serialize(Object obj) throws Exception {
        return ProtobufUtil.encode(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return ProtobufUtil.decode(bytes, clazz);
    }
}
