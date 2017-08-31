/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.web;

import javax.servlet.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet过滤器，设置不使用缓存
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
public class ClearCacheFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // TODO Auto-generated method stub

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws ServletException, IOException {
        HttpServletResponse response = (HttpServletResponse) resp;

        response.addHeader("Pragma", "no-cache");
        response.addHeader("Cache-Control", "no-cache, no-store, max-age=0");
        response.addDateHeader("Expires", 1L);

        chain.doFilter(req, resp);
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}
