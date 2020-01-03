package org.tinygame.herostory.model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户管理器
 */
public final class UserManager {

    /**
     * 用户字典
     */
    private static final Map<Integer, User> userMap = new HashMap<>();

    /**
     * 私有化默认构造器
     */
    private UserManager() {
    }

    /**
     * 添加用户
     *
     * @param user
     */
    public static void addUser(User user) {
        if (user != null) {
            userMap.put(user.getUserId(), user);
        }
    }

    /**
     * 根据ID移除用户
     *
     * @param userId
     */
    public static void removeUserById(int userId) {
        userMap.remove(userId);
    }

    /**
     * 列表用户
     *
     * @return
     */
    public static Collection<User> listUser() {
        return userMap.values();
    }
    /**
     * 根据ID获取用户
     */
    public static User getUserById(int userId){
        return userMap.get(userId);
    }

}
