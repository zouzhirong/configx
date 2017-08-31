package com.configx.web.webmvc;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 登录Cookie工具类
 * <p>
 * Created by zouzhirong on 2016/11/8.
 */
public class LoginCookieUtils {
    /**
     * 设置Cookie
     *
     * @param request
     * @param response
     * @param email
     * @param token
     * @param remember
     */
    public static void addCookie(HttpServletRequest request, HttpServletResponse response,
                                 String email, String token, boolean remember) {
        Cookie cookie = new Cookie("email", email);
        if (remember) {
            cookie.setMaxAge(60 * 60 * 24 * 7);
        } else {
            cookie.setMaxAge(-1);
        }
        //设置路径
        cookie.setPath("/");
        response.addCookie(cookie);


        cookie = new Cookie("token", token);
        if (remember) {
            cookie.setMaxAge(60 * 60 * 24 * 7);
        } else {
            cookie.setMaxAge(-1);
        }
        //设置路径
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    /**
     * 移除Cookie
     *
     * @param request
     * @param response
     */
    public static void removeCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals("email", cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                } else if (StringUtils.equals("token", cookie.getName())) {
                    cookie.setMaxAge(0);
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            }
        }
    }

    /**
     * 获取用户名
     *
     * @return
     */
    public static String getEmail(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), "email")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * 获取Token
     *
     * @return
     */
    public static String getToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (StringUtils.equals(cookie.getName(), "token")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
