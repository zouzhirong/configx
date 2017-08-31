/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.user;

import com.configx.web.dao.UserMapper;
import com.configx.web.exception.ConfigException;
import com.configx.web.exception.ErrorCode;
import com.configx.web.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 用户 Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class UserService {

    /**
     * 登录最大错误次数
     */
    private static final int MAX_ERROR_COUNT = 5;

    /**
     * 登录错误锁定时间
     */
    private static final int ERROR_LOCK_MINUTES = 5;

    @Autowired
    private UserMapper userMapper;

    /**
     * 获取指定ID的用户
     *
     * @param userId
     * @return
     */
    public User getUser(int userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    /**
     * 获取用户
     *
     * @param email
     * @return
     */
    public User getUser(String email) {
        return userMapper.selectUser(email);
    }

    /**
     * 获取用户列表
     *
     * @return
     */
    public List<User> getUserList() {
        return userMapper.selectAll();
    }

    /**
     * 创建新User
     *
     * @param name
     * @param email
     * @param password
     * @return
     */
    public User createUser(String name, String email, String password, boolean isAdmin) {
        User user = newUser(name, email, password, isAdmin);
        return createUser(user);
    }

    /**
     * 创建新User对象
     *
     * @param name
     * @param email
     * @param password
     * @param isAdmin
     * @return
     */
    private User newUser(String name, String email, String password, boolean isAdmin) {

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setAdmin(isAdmin);
        return user;
    }

    /**
     * 创建新User
     *
     * @param user
     * @return
     */
    public User createUser(User user) {
        userMapper.insertSelective(user);
        return user;
    }

    /**
     * 修改User信息
     *
     * @param userId
     * @param name
     * @param password
     * @param isAdmin
     */
    public void modifyUser(int userId,
                           String name,
                           @RequestParam("password") String password,
                           boolean isAdmin) {

        User modifiedUser = new User();
        modifiedUser.setId(userId);
        modifiedUser.setName(name);
        modifiedUser.setPassword(password);
        modifiedUser.setAdmin(isAdmin);

        updateUser(modifiedUser);
    }

    /**
     * 更新User信息
     *
     * @param user
     * @return
     */
    public User updateUser(User user) {
        userMapper.updateByPrimaryKeySelective(user);
        return user;
    }

    /**
     * 删除用户
     *
     * @param userId
     */
    public void delete(int userId) {
        userMapper.deleteByPrimaryKey(userId);
    }

    /**
     * 删除用户
     *
     * @param email
     */
    public void delete(String email) {
        userMapper.deleteUser(email);
    }

    /**
     * 判断用户是否是管理员
     *
     * @param email
     * @return
     */
    public boolean isAdmin(String email) {
        User user = getUser(email);
        return user != null && user.getAdmin();
    }

    /**
     * 登录
     *
     * @param email
     * @param password
     * @return
     */
    public boolean login(String email, String password) {
        // 登录之前
        beforeLogin(email);

        User user = getUser(email);
        boolean success = user != null && user.getPassword().equals(password);

        // 登录之后
        afterLogin(email, success);

        return success;
    }

    /**
     * 登录之前，进行验证
     *
     * @param email
     */
    private void beforeLogin(String email) {
        User user = getUser(email);

        // 错误次数已经达上限
        if (user.getErrorCount() >= MAX_ERROR_COUNT) {

            // 锁定时间已到
            if (user.getLastLoginTime().getTime() + TimeUnit.MINUTES.toMillis(ERROR_LOCK_MINUTES) <= System.currentTimeMillis()) {
                userMapper.clearError(email);
            } else {
                throw new ConfigException(ErrorCode.LOGIN_LOCKED, "login locked", ERROR_LOCK_MINUTES);
            }

        }
    }

    /**
     * 登录之后
     *
     * @param email
     */
    private void afterLogin(String email, boolean success) {
        // 更新登录时间
        userMapper.updateLoginTime(email, new Date());

        if (success) {
            userMapper.clearError(email);
        } else {
            userMapper.incrError(email);

            // 剩余错误次数
            User user = getUser(email);
            int remainErrorCount = MAX_ERROR_COUNT - user.getErrorCount();
            if (remainErrorCount > 0) {
                throw new ConfigException(ErrorCode.LOGIN_ERROR, "login error", remainErrorCount);
            } else {
                throw new ConfigException(ErrorCode.LOGIN_LOCKED, "login locked", ERROR_LOCK_MINUTES);
            }
        }

    }

    /**
     * 修改密码
     *
     * @param email
     * @param password
     */
    public void changePassword(String email, String password) {
        User user = getUser(email);

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);

        updateUser(newUser);
    }
}
