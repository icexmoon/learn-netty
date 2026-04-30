package cn.icexmoon.netty.msgpack.util.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName Message
 * @Description 承载对象进行序列化和反序列化的对象
 *              用于在客户端和服务端之间进行传输
 * @Author icexmoon@qq.com
 * @Date 2026/4/29 18:13
 * @Version 1.0
 */
@Data
public class ObjectMessage implements Serializable {
    /**
     * 将对象序列化后的二进制数组
     */
    private byte[] object;
    /**
     * 对象的完整类名，用于反序列化
     */
    private String className;
}
