package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName GroupCreateResponseMessage
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 15:39
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupCreateResponseMessage {
    private boolean success;
    private String reason;

    public static GroupCreateResponseMessage success() {
        return new GroupCreateResponseMessage(true, null);
    }

    public static GroupCreateResponseMessage fail(String reason) {
        return new GroupCreateResponseMessage(false, reason);
    }
}
