package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName GroupMessageResponse
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 16:13
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupMessageResponse {
    private boolean success;
    private String reason;

    public static GroupMessageResponse success() {
        return new GroupMessageResponse(true, null);
    }

    public static GroupMessageResponse fail(String reason) {
        return new GroupMessageResponse(false, reason);
    }
}
