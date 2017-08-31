/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.privilege;

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
        return appService.isDeveloper(id, email);
    }

    /**
     * 判断用户邮箱是否是指定应用的管理员或开发者
     *
     * @param id
     * @param email
     * @return
     */
    public boolean isAppAdminOrDeveloper(int id, String email) {
        return appService.isAppAdminOrDeveloper(id, email);
    }
}
