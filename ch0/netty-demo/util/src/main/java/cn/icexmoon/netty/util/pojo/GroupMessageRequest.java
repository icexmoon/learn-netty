package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName GroupMessageRequest
 * @Description 群聊消息发送请求
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 16:12
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageRequest {
    private String groupName;
    private String content;
    private String from;
}
