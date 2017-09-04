/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.controller;

import com.configx.web.model.User;
import com.configx.web.page.Page;
import com.configx.web.page.PageRequest;
import com.configx.web.service.privilege.PrivilegeService;
import com.configx.web.service.user.SessionService;
import com.configx.web.service.user.UserContext;
import com.configx.web.service.user.UserService;
import com.configx.web.webmvc.LoginCookieUtils;
import com.configx.web.webmvc.LoginIgnored;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.List;

/**
 * 管理用户Tag Controller
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Controller
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PrivilegeService privilegeService;

    @Autowired
    private SessionService sessionService;

    /**
     * 列出用户列表
     *
     * @param page
     * @param size
     * @return
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public ModelAndView getUserList(@RequestParam(name = "page", required = false, defaultValue = "1") int page,
                                    @RequestParam(name = "size", required = false, defaultValue = "10") int size) {

        List<User> userList = null;

        // 权限认证
        if (privilegeService.isAdministrator(UserContext.email())) {
            userList = userService.getUserList();
        } else {
            userList = Collections.emptyList();
        }

        PageRequest pageRequest = new PageRequest(page, size);
        Page<Void, User> pageResponse = new Page<>(userList, pageRequest);
        return new ModelAndView("user/user").addObject("page", pageResponse);
    }

    /**
     * 获取用户
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/users/{userId}", method = RequestMethod.GET)
    @ResponseBody
    public Object getUser(@PathVariable("userId") int userId) {

        // 权限认证
        privilegeService.assertAdministrator(UserContext.email());

        return userService.getUser(userId);
    }

    /**
     * 创建用户
     *
     * @param name     用户姓名
     * @param email    用户邮箱
     * @param password 用户密码
     * @param isAdmin  是否是管理员
     * @return
     */
    @RequestMapping(value = "/users", method = RequestMethod.POST)
    @ResponseBody
    public Object createUser(@RequestParam("name") String name,
                             @RequestParam("email") String email, @RequestParam("password") String password,
                             @RequestParam(value = "isAdmin", required = false, defaultValue = "false") boolean isAdmin) {

        // 权限认证
        privilegeService.assertAdministrator(UserContext.email());

        userService.createUser(name, email, password, isAdmin);
        return true;
    }

    /**
     * 更改用户
     *
     * @param userId   用户ID
     * @param name     用户姓名
     * @param password 用户密码
     * @param isAdmin  是否是管理员
     * @return
     */
    @RequestMapping(value = "/users/{userId}", method = RequestMethod.PUT)
    @ResponseBody
    public Object modifyUser(@PathVariable("userId") int userId,
                             @RequestParam("name") String name,
                             @RequestParam("password") String password,
                             @RequestParam(value = "isAdmin", required = false, defaultValue = "false") boolean isAdmin) {

        // 权限认证
        privilegeService.assertAdministrator(UserContext.email());

        userService.modifyUser(userId, name, password, isAdmin);
        return true;
    }

    /**
     * 删除用户
     *
     * @param userId 应用ID
     * @return
     */
    @RequestMapping(value = "/users/{userId}", method = RequestMethod.DELETE)
    @ResponseBody
    public Object deleteUser(@PathVariable("userId") int userId) {

        // 权限认证
        privilegeService.assertAdministrator(UserContext.email());

        userService.delete(userId);
        return true;
    }

    /**
     * 登录
     *
     * @param request
     * @param response
     * @return
     */
    @LoginIgnored
    @RequestMapping(value = "/user/login")
    public ModelAndView login(@RequestParam(value = "back", required = false, defaultValue = "") String back,
                              HttpServletRequest request, HttpServletResponse response) {

        ModelAndView mv = new ModelAndView("user/login");

        if (StringUtils.isNotEmpty(back)) {
            mv.addObject("back", back);
        }

        return mv;
    }

    /**
     * 登录提交
     *
     * @param email
     * @param password
     * @return
     */
    @LoginIgnored
    @RequestMapping(value = "/user/login/submit")
    @ResponseBody
    public boolean loginSubmit(@RequestParam("email") String email, @RequestParam("password") String password,
                               @RequestParam(value = "remember", required = false, defaultValue = "false") boolean remember,
                               @RequestParam(value = "back", required = false, defaultValue = "") String back,
                               HttpServletRequest request, HttpServletResponse response) {

        boolean success = userService.login(email, password);

        if (success) {
            // 设置session
            String token = sessionService.createToken(email);
            // 设置cookie
            LoginCookieUtils.addCookie(request, response, email, token, remember);

            return true;

        } else {
            return false;
        }
    }

    /**
     * 退出
     *
     * @return
     */
    @RequestMapping(value = "/user/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        String email = LoginCookieUtils.getEmail(request);

        // 删除session
        sessionService.removeToken(email);
        // 删除cookie
        LoginCookieUtils.removeCookie(request, response);

        return "redirect:/user/login";
    }
}
