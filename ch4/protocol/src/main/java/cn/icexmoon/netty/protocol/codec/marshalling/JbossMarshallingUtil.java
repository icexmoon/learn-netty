package cn.icexmoon.netty.protocol.codec.marshalling;

import org.jboss.marshalling.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @ClassName MarshallingUtil
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/5/13 08:50
 * @Version 1.0
 */
public class JbossMarshallingUtil {
    // 单例工厂（river 协议）
    private static final MarshallerFactory FACTORY = Marshalling.getProvidedMarshallerFactory("river");
    // 配置：使用默认版本（不设置 version），缓存类/实例数
    private static final MarshallingConfiguration CONFIG;

    static {
        CONFIG = new MarshallingConfiguration();
        // 不设置 version，使用默认值
        CONFIG.setClassCount(100);
        CONFIG.setInstanceCount(1000);
    }

    /**
     * 序列化：对象 → byte[]
     */
    public static byte[] encode(Object obj) throws IOException {
        if (obj == null) return new byte[0];
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             Marshaller marshaller = FACTORY.createMarshaller(CONFIG)) {
            marshaller.start(Marshalling.createByteOutput(baos));
            marshaller.writeObject(obj);
            marshaller.finish();
            return baos.toByteArray();
        }
    }

    /**
     * 反序列化：byte[] → 对象
     */
    public static Object decode(byte[] data) throws IOException, ClassNotFoundException {
        if (data == null || data.length == 0) return null;
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             Unmarshaller unmarshaller = FACTORY.createUnmarshaller(CONFIG)) {
            unmarshaller.start(Marshalling.createByteInput(bais));
            Object obj = unmarshaller.readObject();
            unmarshaller.finish();
            return obj;
        }
    }
}
