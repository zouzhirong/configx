/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package ch.qos.logback.classic.helpers;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

import com.configx.web.web.util.WebUtils;

/**
 * A servlet filter that inserts various values retrieved from the incoming http
 * request into the MDC.
 * <p/>
 * <p/>
 * The values are removed after the request is processed.
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
public class MDCServletFilter extends MDCInsertingServletFilter {

	void insertIntoMDC(ServletRequest request) {

		super.insertIntoMDC(request);

		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;

			MDC.put("req.client", WebUtils.getRemoteAddr(httpServletRequest));
		}
	}

	void clearMDC() {
		MDC.clear();
	}

}
