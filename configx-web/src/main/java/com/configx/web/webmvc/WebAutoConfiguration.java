/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.webmvc;

import ch.qos.logback.classic.helpers.MDCServletFilter;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import javax.servlet.Filter;


@Configuration
public class WebAutoConfiguration {

    @Bean
    public FilterRegistrationBean threadLocalFilter() {
        Filter filter = new MDCServletFilter();
        FilterRegistrationBean registration = new FilterRegistrationBean(filter);
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        registration.setName(MDCServletFilter.class.getSimpleName());
        return registration;
    }

    @Configuration
    public static class WebMvcConfigurationAdapter extends WebMvcConfigurerAdapter {
        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(loginInterceptor());
        }

        @Bean
        public HandlerInterceptor loginInterceptor() {
            return new LoginInterceptor();
        }

    }

}
