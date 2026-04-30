package cn.icexmoon.netty.msgpack.util.util;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

/**
 * Protobuf 4.34 专用 POJO 序列化/反序列化工具类
 * 支持任意普通 Java 对象（POJO），无需 .proto，无需生成类
 */
public class ProtobufUtil {

    /**
     * 序列化：对象 → byte[]
     */
    public static <T> byte[] encode(T obj) {
        if (obj == null) {
            return new byte[0];
        }
        Schema<T> schema = (Schema<T>) RuntimeSchema.getSchema(obj.getClass());
        LinkedBuffer buffer = LinkedBuffer.allocate();
        try {
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化：byte[] → 对象
     */
    public static <T> T decode(byte[] data, Class<T> clazz) {
        if (data == null || data.length == 0) {
            return null;
        }
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        T message = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(data, message, schema);
        return message;
    }
}