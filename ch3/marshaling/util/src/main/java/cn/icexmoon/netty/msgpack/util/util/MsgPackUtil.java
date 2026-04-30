package cn.icexmoon.netty.msgpack.util.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.msgpack.jackson.dataformat.MessagePackFactory;

/**
 * @ClassName MsgPackUtil
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/29 18:11
 * @Version 1.0
 */
public class MsgPackUtil {
    private static final ObjectMapper OBJECT_MAPPER;
    
    static {
        OBJECT_MAPPER = new ObjectMapper(new MessagePackFactory());
        // 注册 Java 8 日期时间模块
        OBJECT_MAPPER.registerModule(new JavaTimeModule());
        // 配置 BigDecimal 序列化为普通字符串格式，避免科学计数法
        OBJECT_MAPPER.configure(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN, true);
    }

    public static byte[] encode(Object obj) throws Exception {
        return OBJECT_MAPPER.writeValueAsBytes(obj);
    }

    public static  <T> T decode(byte[] bytes, Class<T> clazz) throws Exception {
        return OBJECT_MAPPER.readValue(bytes, clazz);
    }
}
