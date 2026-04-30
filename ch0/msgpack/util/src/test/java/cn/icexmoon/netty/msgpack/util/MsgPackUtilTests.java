package cn.icexmoon.netty.msgpack.util;

import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import cn.icexmoon.netty.msgpack.util.util.MsgPackUtil;
import lombok.Data;
import org.junit.jupiter.api.Test;

/**
 * @ClassName MsgPackUtilTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 08:44
 * @Version 1.0
 */
public class MsgPackUtilTests {
    @Data
    private static class User {
        private String name;
        private int age;
    }

    @Test
    public void test() throws Exception {
        User user = new User();
        user.setName("icexmoon");
        user.setAge(18);
        byte[] bytes = MsgPackUtil.encode(user);
        User user1 = MsgPackUtil.decode(bytes, User.class);
        System.out.println(user1);
    }
}
