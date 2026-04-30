package cn.icexmoon.netty.msgpack.util.pojo;

import lombok.Data;

import java.time.ZoneId;

/**
 * @ClassName TimeRequest
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 09:03
 * @Version 1.0
 */
@Data
public class TimeRequest {
    private ZoneId zoneId;
}
