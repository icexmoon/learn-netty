package cn.icexmoon.netty.util.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName SimpleCommand
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 16:35
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleCommand {
    public static final String GLIST = "glist";
    private String command;
    private String from;
}
