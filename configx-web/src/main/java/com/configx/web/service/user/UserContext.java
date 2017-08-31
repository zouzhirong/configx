package com.configx.web.service.user;

import com.configx.web.model.User;

/**
 * Created by zouzhirong on 2016/11/11.
 */
public class UserContext {
    /**
     * 当前线程使用的配置版本号
     */
    private static final ThreadLocal<User> currentUser = new ThreadLocal<>();


    /**
     * 返回当前用户的名称
     *
     * @return
     */
    public static String name() {
        User user = currentUser.get();
        return user == null ? "" : user.getName();
    }

    /**
     * 返回当前用户的邮箱
     *
     * @return
     */
    public static String email() {
        User user = currentUser.get();
        return user == null ? "" : user.getEmail();
    }

    /**
     * 设置当前用户
     *
     * @param user
     */
    public static void setCurrentUser(User user) {
        currentUser.set(user);
    }

    /**
     * 清除当前用户
     */
    public static void clearCurrentUser() {
        currentUser.remove();
    }
}
