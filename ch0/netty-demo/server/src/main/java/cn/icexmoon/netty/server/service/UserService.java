package cn.icexmoon.netty.server.service;

import cn.icexmoon.netty.util.pojo.User;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName UserRepository
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2026/4/21 10:34
 * @Version 1.0
 */
public class UserService {
    private static final Map<String, User> users = new HashMap<>();
    static {
        users.put("admin", new User("admin", "123"));
        users.put("user1", new User("user1", "123"));
        users.put("user2", new User("user2", "123"));
        users.put("user3", new User("user3", "123"));
    }

    /**
     * 获取用户
     * @param username 用户名
     * @return 用户
     */
    public static User getUser(String username) {
        return users.get(username);
    }

    /**
     * 判断用户是否存在
     * @param username 用户名
     * @return 存在返回true
     */
    public static boolean isExist(String username) {
        return users.containsKey(username);
    }

    /**
     * 验证密码
     * @param username 用户名
     * @param password 密码
     * @return 验证结果
     */
    public static boolean checkPassword(String username, String password) {
        User user = getUser(username);
        return user != null && user.getPassword().equals(password);
    }
}
