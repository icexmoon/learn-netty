package cn.icexmoon.netty.protocol.codec;

import cn.icexmoon.netty.protocol.pojo.Header;
import cn.icexmoon.netty.protocol.pojo.NettyMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * NettyMessageEncoder 和 NettyMessageDecoder 测试类
 */
@Slf4j
@DisplayName("NettyMessage 编解码器测试")
public class NettyMessageCodecTest {
    
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
    
    private EmbeddedChannel channel;
    private NettyMessageEncoder encoder;
    private NettyMessageDecoder decoder;
    
    @BeforeEach
    @DisplayName("初始化测试环境")
    void setUp() {
        log.info("初始化测试环境");
        encoder = new NettyMessageEncoder();
        // maxFrameLength=1MB, lengthFieldOffset=4(crcCode后), lengthFieldLength=4
        decoder = new NettyMessageDecoder(1024 * 1024, 4, 4);
        channel = new EmbeddedChannel(decoder, encoder);
    }
    
    @AfterEach
    @DisplayName("清理测试资源")
    void tearDown() {
        if (channel != null) {
            channel.finishAndReleaseAll();
            log.info("释放 Channel 资源");
        }
    }
    
    /**
     * 创建测试用的 NettyMessage
     */
    private NettyMessage createTestMessage(long sessionId, byte type, byte priority, 
                                           Map<String, Object> attachment, Object body) {
        Header header = new Header();
        header.setSessionID(sessionId);
        header.setType(type);
        header.setPriority(priority);
        if (attachment != null) {
            header.setAttachment(attachment);
        }
        
        NettyMessage message = new NettyMessage();
        message.setHeader(header);
        message.setBody(body);
        
        return message;
    }
    
    @Test
    @DisplayName("测试基本消息的编解码")
    void testBasicMessageCodec() throws Exception {
        // 创建测试消息
        User user = new User(1L, "张三", 25, "zhangsan@example.com");
        NettyMessage originalMessage = createTestMessage(
            100001L, (byte) 1, (byte) 5, null, user
        );
        
        log.info("原始消息: SessionID={}, Type={}, Priority={}, Body={}", 
                originalMessage.getHeader().getSessionID(),
                originalMessage.getHeader().getType(),
                originalMessage.getHeader().getPriority(),
                originalMessage.getBody());
        
        // 编码
        channel.writeOutbound(originalMessage);
        ByteBuf encodedBuf = channel.readOutbound();
        assertNotNull(encodedBuf, "编码后的 ByteBuf 不应为 null");
        log.info("编码成功，ByteBuf 大小: {}", encodedBuf.readableBytes());
        
        // 打印十六进制数据
        int readableBytes = Math.min(encodedBuf.readableBytes(), 100);
        StringBuilder hexDump = new StringBuilder();
        for (int i = 0; i < readableBytes; i++) {
            if (i > 0 && i % 16 == 0) {
                hexDump.append("\n");
            }
            hexDump.append(String.format("%02X ", encodedBuf.getByte(i)));
        }
        log.debug("编码数据的十六进制表示:\n{}", hexDump);
        
        // 解码
        channel.writeInbound(encodedBuf);
        NettyMessage decodedMessage = channel.readInbound();
        assertNotNull(decodedMessage, "解码后的消息不应为 null");
        
        // 验证 Header
        assertEquals(originalMessage.getHeader().getCrcCode(), 
                    decodedMessage.getHeader().getCrcCode(), "CRC Code 应该一致");
        assertEquals(originalMessage.getHeader().getSessionID(), 
                    decodedMessage.getHeader().getSessionID(), "SessionID 应该一致");
        assertEquals(originalMessage.getHeader().getType(), 
                    decodedMessage.getHeader().getType(), "Type 应该一致");
        assertEquals(originalMessage.getHeader().getPriority(), 
                    decodedMessage.getHeader().getPriority(), "Priority 应该一致");
        
        // 验证 Body
        assertNotNull(decodedMessage.getBody(), "Body 不应为 null");
        User decodedUser = (User) decodedMessage.getBody();
        assertEquals(user.getId(), decodedUser.getId(), "User ID 应该一致");
        assertEquals(user.getName(), decodedUser.getName(), "User Name 应该一致");
        assertEquals(user.getAge(), decodedUser.getAge(), "User Age 应该一致");
        assertEquals(user.getEmail(), decodedUser.getEmail(), "User Email 应该一致");
        
        log.info("✓ 基本消息编解码测试通过");
    }
    
    @Test
    @DisplayName("测试带 Attachment 的消息编解码")
    void testMessageWithAttachment() throws Exception {
        // 创建带附件的消息
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("key1", "value1");
        attachment.put("key2", 12345);
        attachment.put("key3", true);
        
        User user = new User(2L, "李四", 30, "lisi@example.com");
        NettyMessage originalMessage = createTestMessage(
            100002L, (byte) 2, (byte) 3, attachment, user
        );
        
        log.info("原始消息包含 {} 个附件", attachment.size());
        
        // 编码
        channel.writeOutbound(originalMessage);
        ByteBuf encodedBuf = channel.readOutbound();
        assertNotNull(encodedBuf, "编码后的 ByteBuf 不应为 null");
        
        // 解码
        channel.writeInbound(encodedBuf);
        NettyMessage decodedMessage = channel.readInbound();
        assertNotNull(decodedMessage, "解码后的消息不应为 null");
        
        // 验证 Attachment
        Map<String, Object> decodedAttachment = decodedMessage.getHeader().getAttachment();
        assertNotNull(decodedAttachment, "Attachment 不应为 null");
        assertEquals(attachment.size(), decodedAttachment.size(), "Attachment 大小应该一致");
        assertEquals(attachment.get("key1"), decodedAttachment.get("key1"), "key1 应该一致");
        assertEquals(attachment.get("key2"), decodedAttachment.get("key2"), "key2 应该一致");
        assertEquals(attachment.get("key3"), decodedAttachment.get("key3"), "key3 应该一致");
        
        // 验证 Body
        User decodedUser = (User) decodedMessage.getBody();
        assertEquals(user.getId(), decodedUser.getId(), "User ID 应该一致");
        assertEquals(user.getName(), decodedUser.getName(), "User Name 应该一致");
        
        log.info("✓ 带 Attachment 的消息编解码测试通过");
    }
    
    @Test
    @DisplayName("测试无 Body 的消息编解码")
    void testMessageWithoutBody() throws Exception {
        // 创建没有 Body 的消息
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("status", "ok");
        
        NettyMessage originalMessage = createTestMessage(
            100003L, (byte) 3, (byte) 1, attachment, null
        );
        
        log.info("原始消息没有 Body");
        
        // 编码
        channel.writeOutbound(originalMessage);
        ByteBuf encodedBuf = channel.readOutbound();
        assertNotNull(encodedBuf, "编码后的 ByteBuf 不应为 null");
        
        // 解码
        channel.writeInbound(encodedBuf);
        NettyMessage decodedMessage = channel.readInbound();
        assertNotNull(decodedMessage, "解码后的消息不应为 null");
        
        // 验证 Body 为 null
        assertNull(decodedMessage.getBody(), "Body 应该为 null");
        
        // 验证 Attachment
        assertEquals(1, decodedMessage.getHeader().getAttachment().size(), 
                    "Attachment 大小应该为 1");
        
        log.info("✓ 无 Body 的消息编解码测试通过");
    }
    
    @Test
    @DisplayName("测试多个连续消息的编解码")
    void testMultipleMessagesCodec() throws Exception {
        // 创建多个消息
        User user1 = new User(1L, "用户1", 20, "user1@test.com");
        User user2 = new User(2L, "用户2", 25, "user2@test.com");
        User user3 = new User(3L, "用户3", 30, "user3@test.com");
        
        NettyMessage msg1 = createTestMessage(200001L, (byte) 1, (byte) 1, null, user1);
        NettyMessage msg2 = createTestMessage(200002L, (byte) 2, (byte) 2, null, user2);
        NettyMessage msg3 = createTestMessage(200003L, (byte) 3, (byte) 3, null, user3);
        
        // 编码所有消息
        channel.writeOutbound(msg1);
        channel.writeOutbound(msg2);
        channel.writeOutbound(msg3);
        
        // 读取并解码第一个消息
        ByteBuf buf1 = channel.readOutbound();
        channel.writeInbound(buf1);
        NettyMessage decoded1 = channel.readInbound();
        assertNotNull(decoded1, "第一个消息不应为 null");
        assertEquals(200001L, decoded1.getHeader().getSessionID(), "第一个消息 SessionID 应该一致");
        assertEquals(user1, decoded1.getBody(), "第一个消息 Body 应该一致");
        
        // 读取并解码第二个消息
        ByteBuf buf2 = channel.readOutbound();
        channel.writeInbound(buf2);
        NettyMessage decoded2 = channel.readInbound();
        assertNotNull(decoded2, "第二个消息不应为 null");
        assertEquals(200002L, decoded2.getHeader().getSessionID(), "第二个消息 SessionID 应该一致");
        assertEquals(user2, decoded2.getBody(), "第二个消息 Body 应该一致");
        
        // 读取并解码第三个消息
        ByteBuf buf3 = channel.readOutbound();
        channel.writeInbound(buf3);
        NettyMessage decoded3 = channel.readInbound();
        assertNotNull(decoded3, "第三个消息不应为 null");
        assertEquals(200003L, decoded3.getHeader().getSessionID(), "第三个消息 SessionID 应该一致");
        assertEquals(user3, decoded3.getBody(), "第三个消息 Body 应该一致");
        
        log.info("✓ 多个连续消息编解码测试通过");
    }
    
    @Test
    @DisplayName("测试复杂 Attachment 的编解码")
    void testComplexAttachmentCodec() throws Exception {
        // 创建复杂的 Attachment
        Map<String, Object> attachment = new HashMap<>();
        attachment.put("string", "测试字符串");
        attachment.put("integer", 999);
        attachment.put("long", 1234567890L);
        attachment.put("double", 3.14159);
        attachment.put("boolean", true);
        
        User nestedUser = new User(99L, "嵌套用户", 50, "nested@test.com");
        attachment.put("nestedObject", nestedUser);
        
        NettyMessage originalMessage = createTestMessage(
            300001L, (byte) 5, (byte) 10, attachment, "Simple Body"
        );
        
        log.info("原始消息包含 {} 个复杂附件", attachment.size());
        
        // 编码
        channel.writeOutbound(originalMessage);
        ByteBuf encodedBuf = channel.readOutbound();
        
        // 解码
        channel.writeInbound(encodedBuf);
        NettyMessage decodedMessage = channel.readInbound();
        
        // 验证 Attachment
        Map<String, Object> decodedAttachment = decodedMessage.getHeader().getAttachment();
        assertEquals(attachment.size(), decodedAttachment.size(), "Attachment 大小应该一致");
        assertEquals(attachment.get("string"), decodedAttachment.get("string"));
        assertEquals(attachment.get("integer"), decodedAttachment.get("integer"));
        assertEquals(attachment.get("long"), decodedAttachment.get("long"));
        assertEquals(attachment.get("double"), decodedAttachment.get("double"));
        assertEquals(attachment.get("boolean"), decodedAttachment.get("boolean"));
        
        // 验证嵌套对象
        User decodedNestedUser = (User) decodedAttachment.get("nestedObject");
        assertNotNull(decodedNestedUser, "嵌套对象不应为 null");
        assertEquals(nestedUser.getId(), decodedNestedUser.getId());
        assertEquals(nestedUser.getName(), decodedNestedUser.getName());
        
        log.info("✓ 复杂 Attachment 编解码测试通过");
    }
    
    @Test
    @DisplayName("测试空 Attachment 的编解码")
    void testEmptyAttachmentCodec() throws Exception {
        // 创建空 Attachment 的消息
        Map<String, Object> emptyAttachment = new HashMap<>();
        User user = new User(5L, "测试用户", 28, "test@example.com");
        
        NettyMessage originalMessage = createTestMessage(
            400001L, (byte) 1, (byte) 1, emptyAttachment, user
        );
        
        // 编码
        channel.writeOutbound(originalMessage);
        ByteBuf encodedBuf = channel.readOutbound();
        
        // 解码
        channel.writeInbound(encodedBuf);
        NettyMessage decodedMessage = channel.readInbound();
        
        // 验证
        assertNotNull(decodedMessage, "解码消息不应为 null");
        assertEquals(0, decodedMessage.getHeader().getAttachment().size(), 
                    "Attachment 大小应该为 0");
        assertEquals(user, decodedMessage.getBody(), "Body 应该一致");
        
        log.info("✓ 空 Attachment 编解码测试通过");
    }
    
    @Test
    @DisplayName("测试不同消息类型的编解码")
    void testDifferentMessageTypes() throws Exception {
        byte[] types = {0, 1, 50, 100, 127, -1, -50, -128};
        
        for (byte type : types) {
            // 每次循环创建新的编解码器实例
            NettyMessageEncoder testEncoder = new NettyMessageEncoder();
            NettyMessageDecoder testDecoder = new NettyMessageDecoder(1024 * 1024, 4, 4);
            EmbeddedChannel testChannel = new EmbeddedChannel(testDecoder, testEncoder);
            
            User user = new User((long) type, "Type" + type, (int) type, "type" + type + "@test.com");
            NettyMessage originalMessage = createTestMessage(
                500000L + type, type, (byte) 1, null, user
            );
            
            // 编码
            testChannel.writeOutbound(originalMessage);
            ByteBuf encodedBuf = testChannel.readOutbound();
            
            // 解码
            testChannel.writeInbound(encodedBuf);
            NettyMessage decodedMessage = testChannel.readInbound();
            
            assertEquals(type, decodedMessage.getHeader().getType(), 
                        "Type " + type + " 应该一致");
            assertEquals(user, decodedMessage.getBody(), 
                        "Type " + type + " 的 Body 应该一致");
            
            testChannel.finishAndReleaseAll();
        }
        
        log.info("✓ 不同消息类型编解码测试通过");
    }
    
    @Test
    @DisplayName("测试不同优先级的编解码")
    void testDifferentPriorities() throws Exception {
        byte[] priorities = {0, 1, 50, 100, 127, -1, -50, -128};
        
        for (byte priority : priorities) {
            // 每次循环创建新的编解码器实例
            NettyMessageEncoder testEncoder = new NettyMessageEncoder();
            NettyMessageDecoder testDecoder = new NettyMessageDecoder(1024 * 1024, 4, 4);
            EmbeddedChannel testChannel = new EmbeddedChannel(testDecoder, testEncoder);
            
            User user = new User((long) priority, "Priority" + priority, (int) priority,
                                "priority" + priority + "@test.com");
            NettyMessage originalMessage = createTestMessage(
                600000L + priority, (byte) 1, priority, null, user
            );
            
            // 编码
            testChannel.writeOutbound(originalMessage);
            ByteBuf encodedBuf = testChannel.readOutbound();
            
            // 解码
            testChannel.writeInbound(encodedBuf);
            NettyMessage decodedMessage = testChannel.readInbound();
            
            assertEquals(priority, decodedMessage.getHeader().getPriority(), 
                        "Priority " + priority + " 应该一致");
            
            testChannel.finishAndReleaseAll();
        }
        
        log.info("✓ 不同优先级编解码测试通过");
    }
}
