/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.stereotype.Component;

/**
 * Spring ApplicationContext上下文工厂类
 * 
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Component
public class ApplicationContextHelper extends ApplicationObjectSupport {

	private static ApplicationContext context;

	@Override
	protected void initApplicationContext() throws BeansException {
		context = super.getApplicationContext();
	}

	/**
	 * Return the ApplicationContext
	 * 
	 * @return
	 */
	public static ApplicationContext getContext() {
		return context;
	}

	public static <T> T getBean(Class<T> requiredType) {
		return context.getBean(requiredType);
	}

	public static <T> T getBean(String name, Class<T> requiredType) {
		return context.getBean(name, requiredType);
	}
}
