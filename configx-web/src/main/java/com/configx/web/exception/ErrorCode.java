package com.configx.web.exception;

/**
 * Created by zouzhirong on 2017/8/26.
 */
public class ErrorCode {

    /**
     * 没有权限访问
     */
    public static final String ACCESS_DENIED = "access_denied";

    /**
     * 登录错误
     */
    public static final String LOGIN_ERROR = "login_error";

    /**
     * 登录锁定中
     */
    public static final String LOGIN_LOCKED = "login_locked";

    /**
     * Profile默认名字冲突
     */
    public static final String PROFILE_DEFAULT_NAME_CONFLICT = "profile_default_name_conflict";

}
