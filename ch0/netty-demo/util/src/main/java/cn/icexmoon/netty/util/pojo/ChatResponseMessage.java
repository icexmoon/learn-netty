package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ChatResponseMessage
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 10:38
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponseMessage {
    private boolean success;
    private String reason;
}
