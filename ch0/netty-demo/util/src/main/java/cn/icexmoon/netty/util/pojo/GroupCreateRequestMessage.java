package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName GroupCreateRequestMessage
 * @Description 创建群聊请求消息
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 15:11
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCreateRequestMessage {
    /**
     * 创建群聊的用户
     */
    private String creator;
    /**
     * 群聊名称
     */
    private String groupName;
    /**
     * 群聊成员
     */
    private String[] members;
}
