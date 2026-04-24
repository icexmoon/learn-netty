package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName LoginRequestMessage
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 10:02
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestMessage {
    private String username;
    private String password;
}
