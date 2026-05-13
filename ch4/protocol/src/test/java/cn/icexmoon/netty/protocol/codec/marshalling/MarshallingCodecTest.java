package cn.icexmoon.netty.protocol.codec.marshalling;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * MarshallingEncoder 和 MarshallingDecoder 测试类
 */
@Slf4j
@DisplayName("MarshallingEncoder 和 MarshallingDecoder 测试")
public class MarshallingCodecTest {
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private Long id;
        private String name;
        private Integer age;
        private String email;
    }
    
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ComplexObject implements java.io.Serializable {
        private static final long serialVersionUID = 1L;
        private String id;
        private List<String> items;
        private Map<String, Object> properties;
        private User user;
    }
    
    private MarshallingEncoder encoder;
    private MarshallingDecoder decoder;
    private ByteBuf byteBuf;
    
    @BeforeEach
    @DisplayName("初始化测试环境")
    void setUp() {
        log.info("初始化测试环境");
        encoder = new MarshallingEncoder();
        decoder = new MarshallingDecoder();
        byteBuf = Unpooled.buffer();
    }
    
    @AfterEach
    @DisplayName("清理测试资源")
    void tearDown() {
        if (byteBuf != null && byteBuf.refCnt() > 0) {
            byteBuf.release();
            log.info("释放 ByteBuf 资源");
        }
    }
    
    @Test
    @DisplayName("测试基本对象的序列化和反序列化")
    void testBasicObjectSerialization() throws Exception {
        // 创建测试对象
        User user = new User(1L, "张三", 25, "zhangsan@example.com");
        log.info("原始对象: {}", user);
        
        // 序列化对象到 ByteBuf
        encoder.encode(user, byteBuf);
        log.info("序列化成功，ByteBuf 可读字节数: {}", byteBuf.readableBytes());
        
        // 验证 ByteBuf 包含长度前缀（4字节）+ 数据
        assertTrue(byteBuf.readableBytes() > 4, "序列化后的 ByteBuf 应该包含长度前缀和数据");
        
        // 打印十六进制数据（前100字节）
        int readableBytes = Math.min(byteBuf.readableBytes(), 100);
        StringBuilder hexDump = new StringBuilder();
        for (int i = 0; i < readableBytes; i++) {
            if (i > 0 && i % 16 == 0) {
                hexDump.append("\n");
            }
            hexDump.append(String.format("%02X ", byteBuf.getByte(i)));
        }
        log.debug("序列化数据的十六进制表示:\n{}", hexDump);
        
        // 反序列化对象
        User deserializedUser = (User) decoder.decode(byteBuf);
        log.info("反序列化后的对象: {}", deserializedUser);
        
        // 验证对象不为 null
        assertNotNull(deserializedUser, "反序列化后的对象不应为 null");
        
        // 验证对象字段是否一致
        assertEquals(user.getId(), deserializedUser.getId(), "ID 应该一致");
        assertEquals(user.getName(), deserializedUser.getName(), "姓名应该一致");
        assertEquals(user.getAge(), deserializedUser.getAge(), "年龄应该一致");
        assertEquals(user.getEmail(), deserializedUser.getEmail(), "邮箱应该一致");
        
        // 验证对象整体是否一致
        assertEquals(user, deserializedUser, "序列化前后的对象应该一致");
        
        log.info("✓ 基本对象序列化测试通过");
    }
    
    @Test
    @DisplayName("测试多个连续对象的序列化和反序列化")
    void testMultipleObjectsSerialization() throws Exception {
        // 创建多个测试对象
        User user1 = new User(1L, "张三", 25, "zhangsan@example.com");
        User user2 = new User(2L, "李四", 30, "lisi@example.com");
        User user3 = new User(3L, "王五", 28, "wangwu@example.com");
        
        // 序列化第一个对象
        encoder.encode(user1, byteBuf);
        log.info("第一个对象序列化完成，ByteBuf 大小: {}", byteBuf.readableBytes());
        
        // 反序列化第一个对象
        User deserializedUser1 = (User) decoder.decode(byteBuf);
        assertEquals(user1, deserializedUser1, "第一个对象应该一致");
        log.info("第一个对象反序列化成功");
        
        // 序列化第二个对象
        encoder.encode(user2, byteBuf);
        log.info("第二个对象序列化完成，ByteBuf 大小: {}", byteBuf.readableBytes());
        
        // 反序列化第二个对象
        User deserializedUser2 = (User) decoder.decode(byteBuf);
        assertEquals(user2, deserializedUser2, "第二个对象应该一致");
        log.info("第二个对象反序列化成功");
        
        // 序列化第三个对象
        encoder.encode(user3, byteBuf);
        log.info("第三个对象序列化完成，ByteBuf 大小: {}", byteBuf.readableBytes());
        
        // 反序列化第三个对象
        User deserializedUser3 = (User) decoder.decode(byteBuf);
        assertEquals(user3, deserializedUser3, "第三个对象应该一致");
        log.info("第三个对象反序列化成功");
        
        log.info("✓ 多对象连续序列化测试通过");
    }
    
    @Test
    @DisplayName("测试包含 null 字段的对象序列化")
    void testNullFieldSerialization() throws Exception {
        // 创建包含 null 字段的对象
        User user = new User(1L, null, null, null);
        log.info("原始对象（含 null 字段）: {}", user);
        
        // 序列化
        encoder.encode(user, byteBuf);
        log.info("序列化成功，ByteBuf 大小: {}", byteBuf.readableBytes());
        
        // 反序列化
        User deserializedUser = (User) decoder.decode(byteBuf);
        log.info("反序列化后的对象: {}", deserializedUser);
        
        // 验证
        assertNotNull(deserializedUser, "反序列化后的对象不应为 null");
        assertEquals(user.getId(), deserializedUser.getId(), "ID 应该一致");
        assertNull(deserializedUser.getName(), "姓名应该为 null");
        assertNull(deserializedUser.getAge(), "年龄应该为 null");
        assertNull(deserializedUser.getEmail(), "邮箱应该为 null");
        
        log.info("✓ null 字段序列化测试通过");
    }
    
    @Test
    @DisplayName("测试特殊字符的序列化")
    void testSpecialCharactersSerialization() throws Exception {
        // 创建包含特殊字符的对象
        User user = new User(1L, "张三@#$%", 25, "test+user@example.com");
        log.info("原始对象（含特殊字符）: {}", user);
        
        // 序列化
        encoder.encode(user, byteBuf);
        log.info("序列化成功");
        
        // 反序列化
        User deserializedUser = (User) decoder.decode(byteBuf);
        log.info("反序列化后的对象: {}", deserializedUser);
        
        // 验证
        assertEquals(user, deserializedUser, "包含特殊字符的对象应该一致");
        
        log.info("✓ 特殊字符序列化测试通过");
    }
    
    @Test
    @DisplayName("测试复杂嵌套对象的序列化")
    void testComplexObjectSerialization() throws Exception {
        // 创建复杂对象
        User user = new User(1L, "张三", 25, "zhangsan@example.com");
        List<String> items = Arrays.asList("item1", "item2", "item3");
        Map<String, Object> properties = new HashMap<>();
        properties.put("key1", "value1");
        properties.put("key2", 123);
        properties.put("key3", true);
        
        ComplexObject complexObj = new ComplexObject("obj1", items, properties, user);
        log.info("原始复杂对象: {}", complexObj);
        
        // 序列化
        encoder.encode(complexObj, byteBuf);
        log.info("序列化成功，ByteBuf 大小: {}", byteBuf.readableBytes());
        
        // 反序列化
        ComplexObject deserializedObj = (ComplexObject) decoder.decode(byteBuf);
        log.info("反序列化后的复杂对象: {}", deserializedObj);
        
        // 验证
        assertNotNull(deserializedObj, "反序列化后的对象不应为 null");
        assertEquals(complexObj.getId(), deserializedObj.getId(), "ID 应该一致");
        assertEquals(complexObj.getItems(), deserializedObj.getItems(), "列表应该一致");
        assertEquals(complexObj.getProperties(), deserializedObj.getProperties(), "属性映射应该一致");
        assertEquals(complexObj.getUser(), deserializedObj.getUser(), "嵌套用户对象应该一致");
        
        log.info("✓ 复杂对象序列化测试通过");
    }
    
    @Test
    @DisplayName("测试集合类的序列化")
    void testCollectionSerialization() throws Exception {
        // 测试 List
        List<String> list = Arrays.asList("apple", "banana", "orange");
        byteBuf.clear();
        encoder.encode(list, byteBuf);
        List<String> deserializedList = (List<String>) decoder.decode(byteBuf);
        assertEquals(list, deserializedList, "List 应该一致");
        log.info("✓ List 序列化测试通过");
        
        // 测试 Set
        byteBuf.clear();
        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        encoder.encode(set, byteBuf);
        Set<Integer> deserializedSet = (Set<Integer>) decoder.decode(byteBuf);
        assertEquals(set, deserializedSet, "Set 应该一致");
        log.info("✓ Set 序列化测试通过");
        
        // 测试 Map
        byteBuf.clear();
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        encoder.encode(map, byteBuf);
        Map<String, Integer> deserializedMap = (Map<String, Integer>) decoder.decode(byteBuf);
        assertEquals(map, deserializedMap, "Map 应该一致");
        log.info("✓ Map 序列化测试通过");
    }
    
    @Test
    @DisplayName("测试字符串的序列化")
    void testStringSerialization() throws Exception {
        String original = "Hello, World! 你好，世界！";
        encoder.encode(original, byteBuf);
        String deserialized = (String) decoder.decode(byteBuf);
        
        assertEquals(original, deserialized, "字符串应该一致");
        log.info("✓ 字符串序列化测试通过");
    }
    
    @Test
    @DisplayName("测试数字类型的序列化")
    void testNumberSerialization() throws Exception {
        // 测试 Integer
        byteBuf.clear();
        Integer intValue = 12345;
        encoder.encode(intValue, byteBuf);
        Integer deserializedInt = (Integer) decoder.decode(byteBuf);
        assertEquals(intValue, deserializedInt, "Integer 应该一致");
        
        // 测试 Long
        byteBuf.clear();
        Long longValue = 9876543210L;
        encoder.encode(longValue, byteBuf);
        Long deserializedLong = (Long) decoder.decode(byteBuf);
        assertEquals(longValue, deserializedLong, "Long 应该一致");
        
        // 测试 Double
        byteBuf.clear();
        Double doubleValue = 3.14159265358979;
        encoder.encode(doubleValue, byteBuf);
        Double deserializedDouble = (Double) decoder.decode(byteBuf);
        assertEquals(doubleValue, deserializedDouble, "Double 应该一致");
        
        log.info("✓ 数字类型序列化测试通过");
    }
    
    @Test
    @DisplayName("测试空 ByteBuf 的解码")
    void testEmptyByteBufDecode() throws Exception {
        // 创建一个空的 ByteBuf
        ByteBuf emptyBuf = Unpooled.buffer();
        
        // 尝试解码空 ByteBuf，应该返回 null
        Object result = decoder.decode(emptyBuf);
        assertNull(result, "空 ByteBuf 解码应该返回 null");
        
        emptyBuf.release();
        log.info("✓ 空 ByteBuf 解码测试通过");
    }
    
    @Test
    @DisplayName("测试不完整数据的解码")
    void testIncompleteDataDecode() throws Exception {
        // 写入不完整的长度信息（只有2字节，不足4字节）
        ByteBuf incompleteBuf = Unpooled.buffer();
        incompleteBuf.writeShort(100);
        
        // 尝试解码不完整数据，应该返回 null
        Object result = decoder.decode(incompleteBuf);
        assertNull(result, "不完整数据解码应该返回 null");
        
        incompleteBuf.release();
        log.info("✓ 不完整数据解码测试通过");
    }
    
    @Test
    @DisplayName("测试长度与实际数据不匹配的解码")
    void testLengthMismatchDecode() throws Exception {
        // 写入长度前缀为100，但实际数据只有10字节
        ByteBuf mismatchBuf = Unpooled.buffer();
        mismatchBuf.writeInt(100); // 声明长度为100
        mismatchBuf.writeBytes(new byte[10]); // 实际只有10字节
        
        // 尝试解码，应该返回 null
        Object result = decoder.decode(mismatchBuf);
        assertNull(result, "长度与实际数据不匹配时应该返回 null");
        
        mismatchBuf.release();
        log.info("✓ 长度不匹配解码测试通过");
    }
    
    @Test
    @DisplayName("测试大数据对象的序列化")
    void testLargeObjectSerialization() throws Exception {
        // 创建一个大对象
        List<String> largeList = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            largeList.add("Item " + i);
        }
        
        log.info("开始序列化大对象，列表大小: {}", largeList.size());
        encoder.encode(largeList, byteBuf);
        log.info("序列化成功，ByteBuf 大小: {}", byteBuf.readableBytes());
        
        // 反序列化
        List<String> deserializedList = (List<String>) decoder.decode(byteBuf);
        log.info("反序列化成功，列表大小: {}", deserializedList.size());
        
        // 验证
        assertEquals(largeList.size(), deserializedList.size(), "列表大小应该一致");
        assertEquals(largeList, deserializedList, "列表内容应该一致");
        
        log.info("✓ 大数据对象序列化测试通过");
    }
}
