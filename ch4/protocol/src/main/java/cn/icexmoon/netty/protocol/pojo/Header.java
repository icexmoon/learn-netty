package cn.icexmoon.netty.protocol.pojo;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName Header
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/12 17:07
 * @Version 1.0
 */
@Data
public class Header {
    public static class Type{
        public static final byte LOGIN_AUTH_REQ = 0;
        public static final byte LOGIN_AUTH_RESP = 1;
        public static final byte HEARTBEAT_REQ = 2;
        public static final byte HEARTBEAT_RESP = 3;
    }
    private final int crcCode = 0xabef0101;
    /**
     * 消息长度
     */
    private int length;
    /**
     * 会话ID
     */
    private long sessionID;
    /**
     * 消息类型
     */
    private byte type;
    /**
     * 消息优先级
     */
    private byte priority;
    /**
     * 附件
     */
    private Map<String, Object> attachment = new HashMap<>();
}
