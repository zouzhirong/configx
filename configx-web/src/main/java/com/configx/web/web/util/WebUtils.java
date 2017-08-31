/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web.util;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.alibaba.fastjson.JSONObject;

/**
 * Miscellaneous utilities for web applications.
 * Used by various framework classes.
 * 
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
public class WebUtils extends org.springframework.web.util.WebUtils {
	/**
	 * /**
	 * 返回客户端的Internet协议（IP）地址或发送请求最后一个代理。
	 * 
	 * x-forwarded-for
	 * 这一HTTP头一般格式如下:
	 * X-Forwarded-For: client1, proxy1, proxy2
	 * 其中的值通过一个 逗号+空格 把多个IP地址区分开, 最左边(client1)是最原始客户端的IP地址,
	 * 代理服务器每成功收到一个请求，就把请求来源IP地址添加到右边。
	 * 
	 * @param request
	 * @return
	 */
	public static String getRemoteAddr(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * 参数转json
	 * 
	 * @param request
	 * @return
	 */
	public static JSONObject paramToJson(HttpServletRequest request) {
		JSONObject json = new JSONObject();
		Map<String, String[]> param = request.getParameterMap();
		for (Map.Entry<String, String[]> entry : param.entrySet()) {
			json.put(entry.getKey(), entry.getValue()[0]);
		}
		return json;
	}

	/**
	 * 获取basePath
	 * 
	 * @param request
	 * @return
	 */
	public static String getBasePath(HttpServletRequest request) {
		String path = request.getContextPath();
		StringBuilder basePath = new StringBuilder();
		basePath.append(request.getScheme()).append("://").append(request.getServerName());
		if (request.getServerPort() != 80) {
			basePath.append(":").append(request.getServerPort());
		}
		basePath.append(path + "/");
		return basePath.toString();
	}

	protected static final String FORM_CHARSET = "UTF-8";

	/**
	 * Use {@link javax.servlet.ServletRequest#getParameterMap()} to reconstruct the
	 * body of a form 'POST' providing a predictable outcome as opposed to reading
	 * from the body, which can fail if any other code has used ServletRequest
	 * to access a parameter thus causing the input stream to be "consumed".
	 */
	public static String getBodyFromServletRequestParameters(HttpServletRequest request) throws IOException {
		StringBuilder builder = new StringBuilder();
		Map<String, String[]> form = request.getParameterMap();
		for (Iterator<String> nameIterator = form.keySet().iterator(); nameIterator.hasNext();) {
			String name = nameIterator.next();
			List<String> values = Arrays.asList(form.get(name));
			for (Iterator<String> valueIterator = values.iterator(); valueIterator.hasNext();) {
				String value = valueIterator.next();
				builder.append(URLEncoder.encode(name, FORM_CHARSET));
				if (value != null) {
					builder.append('=');
					builder.append(URLEncoder.encode(value, FORM_CHARSET));
					if (valueIterator.hasNext()) {
						builder.append('&');
					}
				}
			}
			if (nameIterator.hasNext()) {
				builder.append('&');
			}
		}

		return builder.toString();
	}

}
