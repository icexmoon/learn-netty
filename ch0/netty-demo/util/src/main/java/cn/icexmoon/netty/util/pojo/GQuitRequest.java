package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName GQuitRequest
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 17:45
 * @Version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GQuitRequest {
    private String groupName;
    private String userName;
}
