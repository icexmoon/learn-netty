package cn.icexmoon.netty.msgpack.util.pojo;

import lombok.Data;

/**
 * @ClassName Message
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/29 18:13
 * @Version 1.0
 */
@Data
public class ObjectMessage {
    private byte[] object;
    private String className;
}
