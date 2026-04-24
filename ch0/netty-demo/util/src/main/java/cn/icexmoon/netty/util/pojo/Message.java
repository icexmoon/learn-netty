package cn.icexmoon.netty.util.pojo;

import lombok.Data;

/**
 * @ClassName Message
 * @Description 用来包裹任意 POJO，在网络上传输
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 14:11
 * @Version 1.0
 */
@Data
public class Message {
    // 完整类名，例如：com.example.UserMsg
    private String className;
    // POJO 转的 JSON
    private String data;
}
