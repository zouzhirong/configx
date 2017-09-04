/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.privilege;

import com.configx.web.exception.ConfigException;
import com.configx.web.exception.ErrorCode;
import com.configx.web.service.app.AppService;
import com.configx.web.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 特权 Service
 *
 * @author Zhirong Zou
 */
@Service
public class PrivilegeService {

    @Autowired
    private AppService appService;

    @Autowired
    private UserService userService;

    /**
     * 判断用户邮箱是否是系统管理员
     *
     * @param email
     * @return
     */
    public boolean isAdministrator(String email) {
        return userService.isAdmin(email);
    }

    /**
     * 判断用户邮箱是否是指定应用的管理员
     *
     * @param id
     * @param email
     * @return
     */
    public boolean isAppAdmin(int id, String email) {
        if (isAdministrator(email)) {
            return true;
        }
        return appService.isAdmin(id, email);
    }

    /**
     * 判断用户邮箱是否是指定应用的开发者
     *
     * @param id
     * @param email
     * @return
     */
    public boolean isAppDeveloper(int id, String email) {
        if (isAppAdmin(id, email)) {
            return true;
        }
        return appService.isDeveloper(id, email);
    }

    /**
     * 断言用户邮箱是系统管理员
     *
     * @param email
     */
    public void assertAdministrator(String email) {
        if (isAdministrator(email)) {
            return;
        }
        throw new ConfigException(ErrorCode.ACCESS_DENIED, "access denied");
    }

    /**
     * 断言用户邮箱是指定应用的管理员
     *
     * @param id
     * @param email
     */
    public void assertAppAdmin(int id, String email) {
        if (isAppAdmin(id, email)) {
            return;
        }
        throw new ConfigException(ErrorCode.ACCESS_DENIED, "access denied");
    }

    /**
     * 断言用户邮箱是指定应用的开发者
     *
     * @param id
     * @param email
     */
    public void assertAppDeveloper(int id, String email) {
        if (isAppDeveloper(id, email)) {
            return;
        }
        throw new ConfigException(ErrorCode.ACCESS_DENIED, "access denied");
    }

}
