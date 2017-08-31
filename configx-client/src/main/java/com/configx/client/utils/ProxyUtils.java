/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.client.utils;

import org.springframework.aop.framework.Advised;

/**
 * 代理工具类
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
public class ProxyUtils {
    /**
     * 获取经过CGLIB包装过的Target Bean
     *
     * @param proxy
     * @param targetClass
     * @return
     * @throws Exception
     */
    @SuppressWarnings({"unchecked"})
    public static <T> T getTargetObject(Object proxy, Class<T> targetClass) throws Exception {

        return (T) ((Advised) proxy).getTargetSource().getTarget();
    }
}
