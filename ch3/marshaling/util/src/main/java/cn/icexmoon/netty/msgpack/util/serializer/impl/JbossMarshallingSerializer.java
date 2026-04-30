package cn.icexmoon.netty.msgpack.util.serializer.impl;

import cn.icexmoon.netty.msgpack.util.serializer.ISerializer;
import cn.icexmoon.netty.msgpack.util.util.JbossMarshallingUtil;

/**
 * @ClassName JbossMarshallingSerializer
 * @Description 使用 Jboss Marshalling 进行序列化
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 14:14
 * @Version 1.0
 */
public class JbossMarshallingSerializer implements ISerializer {
    @Override
    public byte[] serialize(Object obj) throws Exception {
        return JbossMarshallingUtil.encode(obj);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception {
        return JbossMarshallingUtil.decode(bytes, clazz);
    }
}
