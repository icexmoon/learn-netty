package cn.icexmoon.netty.msgpack.util.serializer.impl;

import cn.icexmoon.netty.msgpack.util.serializer.ISerializer;
import cn.icexmoon.netty.msgpack.util.util.MsgPackUtil;

/**
 * @ClassName MsgPackSerializer
 * @Description 使用MsgPack进行序列化
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 14:11
 * @Version 1.0
 */
public class MsgPackSerializer implements ISerializer {
    @Override
    public byte[] serialize(Object obj) throws Exception {
        return MsgPackUtil.encode(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return MsgPackUtil.decode(bytes, clazz);
    }
}
