package cn.icexmoon.learnnetty.utils.pojo;

import java.util.Date;

/**
 * @ClassName UnixTime
 * @Description 表示时间的 POJO
 * @Author icexmoon@qq.com
 * @Date 2025/12/19 下午2:21
 * @Version 1.0
 */
public class UnixTime {
    private final long value;

    public UnixTime() {
        this(System.currentTimeMillis() / 1000L + 2208988800L);
    }

    public UnixTime(long value) {
        this.value = value;
    }

    public long value() {
        return value;
    }

    @Override
    public String toString() {
        return new Date((value() - 2208988800L) * 1000L).toString();
    }
}
