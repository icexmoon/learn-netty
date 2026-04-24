package cn.icexmoon.netty.server.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

/**
 * @ClassName ChatGroup
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/24 15:24
 * @Version 1.0
 */
@Data
@NoArgsConstructor
public class ChatGroup {
    private List<String> users = Collections.synchronizedList(new ArrayList<>());
    private String name;

    public ChatGroup(String name, Set<String> users) {
        this.name = name;
        this.users.addAll(users);
    }

    public void addUser(String userName) {
        if (users.contains(userName)) {
            return;
        }
        users.add(userName);
    }

    public void removeUser(String userName) {
        users.remove(userName);
    }
}
