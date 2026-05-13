package cn.icexmoon.netty.protocol.codec.marshalling;

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
 * JbossMarshallingUtil 工具类测试类
 */
@Slf4j
@DisplayName("JbossMarshallingUtil 工具类测试")
public class JbossMarshallingUtilTest {
    
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
    
    @BeforeEach
    @DisplayName("初始化测试环境")
    void setUp() {
        log.info("初始化测试环境");
    }
    
    @AfterEach
    @DisplayName("清理测试资源")
    void tearDown() {
        log.info("清理测试资源");
    }
    
    @Test
    @DisplayName("测试基本对象的序列化和反序列化")
    void testBasicSerialization() throws Exception {
        // 创建测试对象
        User user = new User(1L, "张三", 25, "zhangsan@example.com");
        log.info("原始对象: {}", user);
        
        // 序列化对象到 byte[]
        byte[] data = JbossMarshallingUtil.encode(user);
        log.info("序列化成功，字节数组长度: {}", data.length);
        
        // 验证字节数组不为空
        assertNotNull(data, "序列化后的字节数组不应为 null");
        assertTrue(data.length > 0, "序列化后的字节数组应该包含数据");
        
        // 打印十六进制数据（前100字节）
        int readableBytes = Math.min(data.length, 100);
        StringBuilder hexDump = new StringBuilder();
        for (int i = 0; i < readableBytes; i++) {
            if (i > 0 && i % 16 == 0) {
                hexDump.append("\n");
            }
            hexDump.append(String.format("%02X ", data[i]));
        }
        log.debug("序列化数据的十六进制表示:\n{}", hexDump);
        
        // 反序列化对象
        User deserializedUser = (User) JbossMarshallingUtil.decode(data);
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
        
        log.info("✓ 基本序列化测试通过");
    }
    
    @Test
    @DisplayName("测试多个对象的序列化和反序列化")
    void testMultipleObjectsSerialization() throws Exception {
        // 创建多个测试对象
        User user1 = new User(1L, "张三", 25, "zhangsan@example.com");
        User user2 = new User(2L, "李四", 30, "lisi@example.com");
        User user3 = new User(3L, "王五", 28, "wangwu@example.com");
        
        // 序列化第一个对象
        byte[] data1 = JbossMarshallingUtil.encode(user1);
        User deserializedUser1 = (User) JbossMarshallingUtil.decode(data1);
        assertEquals(user1, deserializedUser1, "第一个对象应该一致");
        
        // 序列化第二个对象
        byte[] data2 = JbossMarshallingUtil.encode(user2);
        User deserializedUser2 = (User) JbossMarshallingUtil.decode(data2);
        assertEquals(user2, deserializedUser2, "第二个对象应该一致");
        
        // 序列化第三个对象
        byte[] data3 = JbossMarshallingUtil.encode(user3);
        User deserializedUser3 = (User) JbossMarshallingUtil.decode(data3);
        assertEquals(user3, deserializedUser3, "第三个对象应该一致");
        
        log.info("✓ 多对象序列化测试通过");
    }
    
    @Test
    @DisplayName("测试 null 字段的序列化")
    void testNullFieldSerialization() throws Exception {
        // 创建包含 null 字段的对象
        User user = new User(1L, null, null, null);
        log.info("原始对象（含 null 字段）: {}", user);
        
        // 序列化
        byte[] data = JbossMarshallingUtil.encode(user);
        
        // 反序列化
        User deserializedUser = (User) JbossMarshallingUtil.decode(data);
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
        byte[] data = JbossMarshallingUtil.encode(user);
        
        // 反序列化
        User deserializedUser = (User) JbossMarshallingUtil.decode(data);
        log.info("反序列化后的对象: {}", deserializedUser);
        
        // 验证
        assertEquals(user, deserializedUser, "包含特殊字符的对象应该一致");
        
        log.info("✓ 特殊字符序列化测试通过");
    }
    
    @Test
    @DisplayName("测试复杂对象的序列化和反序列化")
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
        byte[] data = JbossMarshallingUtil.encode(complexObj);
        log.info("序列化成功，字节数组长度: {}", data.length);
        
        // 反序列化
        ComplexObject deserializedObj = (ComplexObject) JbossMarshallingUtil.decode(data);
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
    @DisplayName("测试空对象的序列化")
    void testNullObjectSerialization() throws Exception {
        // 序列化 null 对象
        byte[] data = JbossMarshallingUtil.encode(null);
        
        // 验证返回空字节数组
        assertNotNull(data, "序列化 null 对象应返回非 null 的字节数组");
        assertEquals(0, data.length, "序列化 null 对象应返回空字节数组");
        
        // 反序列化空字节数组
        Object result = JbossMarshallingUtil.decode(new byte[0]);
        assertNull(result, "反序列化空字节数组应返回 null");
        
        // 反序列化 null
        Object result2 = JbossMarshallingUtil.decode(null);
        assertNull(result2, "反序列化 null 应返回 null");
        
        log.info("✓ 空对象序列化测试通过");
    }
    
    @Test
    @DisplayName("测试集合类的序列化和反序列化")
    void testCollectionSerialization() throws Exception {
        // 测试 List
        List<String> list = Arrays.asList("apple", "banana", "orange");
        byte[] listData = JbossMarshallingUtil.encode(list);
        List<String> deserializedList = (List<String>) JbossMarshallingUtil.decode(listData);
        assertEquals(list, deserializedList, "List 应该一致");
        
        // 测试 Set
        Set<Integer> set = new HashSet<>(Arrays.asList(1, 2, 3, 4, 5));
        byte[] setData = JbossMarshallingUtil.encode(set);
        Set<Integer> deserializedSet = (Set<Integer>) JbossMarshallingUtil.decode(setData);
        assertEquals(set, deserializedSet, "Set 应该一致");
        
        // 测试 Map
        Map<String, Integer> map = new HashMap<>();
        map.put("one", 1);
        map.put("two", 2);
        map.put("three", 3);
        byte[] mapData = JbossMarshallingUtil.encode(map);
        Map<String, Integer> deserializedMap = (Map<String, Integer>) JbossMarshallingUtil.decode(mapData);
        assertEquals(map, deserializedMap, "Map 应该一致");
        
        log.info("✓ 集合类序列化测试通过");
    }
    
    @Test
    @DisplayName("测试字符串的序列化和反序列化")
    void testStringSerialization() throws Exception {
        String original = "Hello, World! 你好，世界！";
        byte[] data = JbossMarshallingUtil.encode(original);
        String deserialized = (String) JbossMarshallingUtil.decode(data);
        
        assertEquals(original, deserialized, "字符串应该一致");
        
        log.info("✓ 字符串序列化测试通过");
    }
    
    @Test
    @DisplayName("测试数字类型的序列化和反序列化")
    void testNumberSerialization() throws Exception {
        // 测试 Integer
        Integer intValue = 12345;
        byte[] intData = JbossMarshallingUtil.encode(intValue);
        Integer deserializedInt = (Integer) JbossMarshallingUtil.decode(intData);
        assertEquals(intValue, deserializedInt, "Integer 应该一致");
        
        // 测试 Long
        Long longValue = 9876543210L;
        byte[] longData = JbossMarshallingUtil.encode(longValue);
        Long deserializedLong = (Long) JbossMarshallingUtil.decode(longData);
        assertEquals(longValue, deserializedLong, "Long 应该一致");
        
        // 测试 Double
        Double doubleValue = 3.14159265358979;
        byte[] doubleData = JbossMarshallingUtil.encode(doubleValue);
        Double deserializedDouble = (Double) JbossMarshallingUtil.decode(doubleData);
        assertEquals(doubleValue, deserializedDouble, "Double 应该一致");
        
        log.info("✓ 数字类型序列化测试通过");
    }
}
