package cn.icexmoon.netty.server.service;

import cn.icexmoon.netty.server.pojo.ChatGroup;
import lombok.Getter;
import lombok.NonNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ChatGroupService
 * @Description 聊天群服务
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 15:23
 * @Version 1.0
 */
public class ChatGroupService {
    private static final Map<String, ChatGroup> chatGroups = new ConcurrentHashMap<>();

    public static boolean groupExist(String groupName) {
        return chatGroups.containsKey(groupName);
    }

    public static ChatGroup getChatGroup(String groupName) {
        return chatGroups.get(groupName);
    }

    public static void addChatGroup(@NonNull ChatGroup chatGroup) {
        chatGroups.put(chatGroup.getName(), chatGroup);
    }
}
