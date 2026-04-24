package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ChatRequestMessage
 * @Description 用于聊天的请求信息
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 10:31
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChatRequestMessage {
    private String from;
    private String to;
    private String content;
}
