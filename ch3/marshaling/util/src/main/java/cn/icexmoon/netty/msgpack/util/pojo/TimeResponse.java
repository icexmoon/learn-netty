package cn.icexmoon.netty.msgpack.util.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName TimeResponse
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 09:07
 * @Version 1.0
 */
@Data
public class TimeResponse implements Serializable {
    // 使用字符串格式存储时间，避免 MessagePack 序列化 BigDecimal 精度问题
    private String time;
    private String zoneId;
    
    @JsonIgnore
    public ZonedDateTime getZonedDateTime() {
        if (time == null || zoneId == null) {
            return null;
        }
        return ZonedDateTime.parse(time, DateTimeFormatter.ISO_ZONED_DATE_TIME);
    }

    @JsonIgnore
    public void setZonedDateTime(ZonedDateTime zonedDateTime) {
        if (zonedDateTime != null) {
            this.time = zonedDateTime.format(DateTimeFormatter.ISO_ZONED_DATE_TIME);
            this.zoneId = zonedDateTime.getZone().getId();
        }
    }
}
