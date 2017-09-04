package com.configx.web.service.privilege;

import com.configx.web.service.user.UserContext;
import com.configx.web.support.ApplicationContextHelper;

/**
 * Created by zouzhirong on 2017/9/4.
 */
public class PrivilegeUtils {

    /**
     * 判断用户邮箱是否是系统管理员
     *
     * @return
     */
    public static boolean isAdministrator() {
        return getPrivilegeService().isAdministrator(UserContext.email());
    }

    /**
     * 判断用户邮箱是否是指定应用的管理员
     *
     * @param id
     * @return
     */
    public static boolean isAppAdmin(int id) {
        return getPrivilegeService().isAppAdmin(id, UserContext.email());
    }

    /**
     * 判断用户邮箱是否是指定应用的开发者
     *
     * @param id
     * @return
     */
    public static boolean isAppDeveloper(int id) {
        return getPrivilegeService().isAppDeveloper(id, UserContext.email());
    }

    private static PrivilegeService getPrivilegeService() {
        return ApplicationContextHelper.getBean(PrivilegeService.class);
    }
}
