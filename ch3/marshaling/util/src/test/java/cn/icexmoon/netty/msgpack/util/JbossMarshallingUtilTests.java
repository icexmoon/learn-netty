package cn.icexmoon.netty.msgpack.util;

import cn.icexmoon.netty.msgpack.util.pojo.ObjectMessage;
import cn.icexmoon.netty.msgpack.util.util.JbossMarshallingUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;

/**
 * @ClassName JbossMarshallingUtilTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/30 11:16
 * @Version 1.0
 */
public class JbossMarshallingUtilTests {
    @Test
    void test() throws IOException, ClassNotFoundException {
        ObjectMessage objectMessage = new ObjectMessage();
        objectMessage.setClassName("java.lang.String");
        objectMessage.setObject("hello world".getBytes());
        byte[] bytes = JbossMarshallingUtil.encode(objectMessage);
        ObjectMessage objectMessage1 = JbossMarshallingUtil.decode(bytes, ObjectMessage.class);
        System.out.println(objectMessage1);
    }
}
