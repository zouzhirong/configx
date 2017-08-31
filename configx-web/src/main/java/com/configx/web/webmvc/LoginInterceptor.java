/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.webmvc;

import com.configx.web.model.User;
import com.configx.web.service.user.UserService;
import com.configx.web.support.ApplicationContextHelper;
import com.configx.web.model.App;
import com.configx.web.service.app.AppService;
import com.configx.web.service.user.SessionService;
import com.configx.web.service.user.UserContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 登陆拦截器
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
public class LoginInterceptor extends HandlerInterceptorAdapter {

    public final boolean supports(Object handler) {
        return handler instanceof HandlerMethod;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!supports(handler)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        Class<?> handlerClass = handlerMethod.getMethod().getDeclaringClass();
        LoginIgnored annotation = handlerMethod.getMethodAnnotation(LoginIgnored.class);
        if (annotation == null) {
            annotation = AnnotationUtils.findAnnotation(handlerClass, LoginIgnored.class);
        }
        if (annotation != null) {
            return true;
        }

        return doLogin(request, response);
    }

    /**
     * 登录检测
     *
     * @param request
     * @param response
     * @return true通过，false未通过
     */
    private boolean doLogin(HttpServletRequest request, HttpServletResponse response) {
        String email = LoginCookieUtils.getEmail(request);
        String cookieToken = LoginCookieUtils.getToken(request);

        SessionService sessionService = ApplicationContextHelper.getBean(SessionService.class);

        boolean success = false;
        if (StringUtils.isNotEmpty(email) && StringUtils.isNotEmpty(cookieToken)) {
            String sessionToken = sessionService.getToken(email);
            success = cookieToken != null && cookieToken.equals(sessionToken); // 与服务器的token进行校验
        }

        if (success) {
            onLoggedIn(email, request, response); // 已登录
        } else {
            onNotLoggedIn(email, request, response); // 未登录
        }

        return success;
    }

    /**
     * 已登录
     *
     * @param email
     * @param request
     * @param response
     */
    private void onLoggedIn(String email, HttpServletRequest request, HttpServletResponse response) {
        User user = ApplicationContextHelper.getBean(UserService.class).getUser(email);

        // 设置到线程变量中
        UserContext.setCurrentUser(user);

        AppService appService = ApplicationContextHelper.getBean(AppService.class);
        List<App> userAppList = appService.getUserAppList(UserContext.email());

        Map pathVariables = (Map) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);

        App currentApp = null;
        if (pathVariables.containsKey("appId")) {
            int appId = Integer.valueOf((String) pathVariables.get("appId"));
            currentApp = appService.getApp(appId);
        } else {
            currentApp = appService.getDefaultApp(user.getEmail());
        }

        // 设置到request attribute中
        request.setAttribute("name", user.getName());
        request.setAttribute("email", user.getEmail());
        request.setAttribute("isAdmin", user.getAdmin());
        request.setAttribute("userAppList", userAppList);
        request.setAttribute("currentApp", currentApp);
    }

    /**
     * 未登录
     *
     * @param email
     * @param request
     * @param response
     */
    private void onNotLoggedIn(String email, HttpServletRequest request, HttpServletResponse response) {
        try {
            String back = request.getRequestURI();
            if (StringUtils.isEmpty(back) || back.contains("/user/logout")) {
                response.sendRedirect("/user/login");
            } else {
                response.sendRedirect("/user/login?back=" + back);
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // TODO Auto-generated method stub
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

        // 清除线程变量
        UserContext.clearCurrentUser();

        super.afterCompletion(request, response, handler, ex);
    }

}
