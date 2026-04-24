package cn.icexmoon.netty.util.pojo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName LoginResponseMessage
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 10:45
 * @Version 1.0
 */
@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor
public class LoginResponseMessage {
    private boolean success;
    private String reason;
    private String username;

    public static LoginResponseMessage success(String username) {
        return new LoginResponseMessage(true, "登录成功", username);
    }

    public static LoginResponseMessage fail(String reason) {
        return new LoginResponseMessage(false, reason, null);
    }
}
