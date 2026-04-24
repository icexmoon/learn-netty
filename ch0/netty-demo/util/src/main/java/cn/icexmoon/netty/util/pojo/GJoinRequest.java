package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName GJoinRequest
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 17:27
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GJoinRequest {
    private String groupName;
    private String userName;
}
