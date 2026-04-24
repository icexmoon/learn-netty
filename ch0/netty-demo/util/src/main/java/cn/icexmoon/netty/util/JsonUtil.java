package cn.icexmoon.netty.util;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @ClassName JsonUtil
 * @Description JSON 工具类
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 14:12
 * @Version 1.0
 */
public class JsonUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 对象 → JSON
     * @param obj 对象
     * @return  JSON
     * @throws Exception 抛出异常
     */
    public static String toJson(Object obj) throws Exception {
        return MAPPER.writeValueAsString(obj);
    }

    /**
     * JSON → 指定 Class 对象
     * @param json  JSON
     * @param clazz  Class
     * @return Class 对象
     * @param <T> 泛型
     * @throws Exception 抛出异常
     */
    public static <T> T toBean(String json, Class<T> clazz) throws Exception {
        return MAPPER.readValue(json, clazz);
    }
}