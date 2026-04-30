package cn.icexmoon.netty.msgpack.util.serializer;

/**
 * @ClassName ISerializer
 * @Description 自定义序列化接口
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 14:10
 * @Version 1.0
 */
public interface ISerializer {
    byte[] serialize(Object obj) throws Exception;
    <T> T deserialize(byte[] bytes, Class<T> clazz) throws Exception;
}
