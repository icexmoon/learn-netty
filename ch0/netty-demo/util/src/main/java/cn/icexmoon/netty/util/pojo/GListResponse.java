package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @ClassName GListResponse
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 16:52
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class GListResponse {
    private List<String> users;
}
