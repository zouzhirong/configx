/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.webmvc;

import com.alibaba.fastjson.JSON;
import com.configx.web.exception.ConfigException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 异常
 */
@ControllerAdvice
public class ExceptionHandlerAdvice extends ApplicationObjectSupport {

    private final Logger logger = LoggerFactory.getLogger(getClass().getPackage().getName());

    private static MessageSourceAccessor messages;

    @Override
    protected void initApplicationContext() throws BeansException {
        messages = super.getMessageSourceAccessor();
    }

    /**
     * 处理ConfigException异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(ConfigException.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ErrorResponse handleConfigException(HttpServletRequest request, ConfigException e) {
        // 记录exception
        error("ConfigException(code=" + e.getCode() + ")", request, e);

        String code = e.getCode();
        String msg = getMsg(code, e.getArgs());
        return newError(code, msg, e.getError());
    }

    /**
     * 处理未知异常
     *
     * @param request
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public ErrorResponse handleException(HttpServletRequest request, Exception e) {
        // 记录exception
        error("", request, e);

        String code = "exception";
        String msg = getMsg(code, null);
        return newError(code, msg, "");
    }

    /**
     * 打印异常日志
     *
     * @param msg
     * @param request
     * @param e
     */
    private void error(String msg, HttpServletRequest request, Exception e) {
        String uri = getRequestURI(request);
        String params = getRequestParams(request);

        // 记录exception
        if (e == null) {
            logger.warn(uri + " " + params + " " + msg);
        } else {
            logger.error(uri + " " + params + " " + msg, e);
        }
    }

    /**
     * 返回请求URI
     *
     * @param request
     * @return
     */
    private String getRequestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    /**
     * 返回请求参数
     *
     * @param request
     * @return
     */
    private String getRequestParams(HttpServletRequest request) {
        Map<String, Object> parameterMap = new HashMap<>();

        // http request中的参数
        Map<String, Object> requestparameterMap = WebUtils.getParametersStartingWith(request, "");
        parameterMap.putAll(requestparameterMap);

        return JSON.toJSONString(parameterMap);
    }

    /**
     * 返回异常的错误描述
     *
     * @param code
     * @param args
     * @return
     */
    private String getMsg(String code, Object[] args) {
        String defaultMsg = messages.getMessage("error.code.exception");
        return messages.getMessage("error.code." + code, args, defaultMsg);
    }

    /**
     * 构造错误响应
     *
     * @param code
     * @param msg
     * @param error
     * @return
     */
    private ErrorResponse newError(String code, String msg, String error) {
        ErrorResponse e = new ErrorResponse();
        // 设置错误码
        e.setCode(code);
        // 设置错误提示
        if (msg != null) {
            e.setMsg(msg);
        }
        // 设置异常信息
        if (error != null) {
            e.setError(error);
        }
        return e;
    }

}
