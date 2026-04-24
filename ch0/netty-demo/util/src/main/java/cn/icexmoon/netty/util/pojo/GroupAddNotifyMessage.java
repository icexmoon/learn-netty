package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName GroupAddNotifyMessage
 * @Description 聊天室加入通知
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 15:54
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupAddNotifyMessage {
    private String groupName;
}
