package cn.icexmoon.netty.protocol.pojo;

import lombok.Data;

/**
 * @ClassName NettyMessage
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/12 17:06
 * @Version 1.0
 */
@Data
public class NettyMessage {
    private Header header;
    private Object body;
}
