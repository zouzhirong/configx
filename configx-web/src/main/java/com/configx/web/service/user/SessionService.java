/**
 * $Id$
 * Copyright(C) 2012-2016 configx.com. All rights reserved.
 */
package com.configx.web.service.user;

import com.configx.web.dao.SessionMapper;
import com.configx.web.model.Session;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Session Service
 *
 * @author <a href="mailto:zhirong.zou@configx.com">zhirong.zou</a>
 * @version 1.0
 * @since 1.0
 */
@Service
public class SessionService {

    @Autowired
    private SessionMapper sessionMapper;

    /**
     * 创建token
     *
     * @param email
     */
    public String createToken(String email) {
        String token = getToken(email);
        if (StringUtils.isNotEmpty(token)) {
            return token;
        }

        // 分配token
        token = RandomStringUtils.randomAlphanumeric(32);
        Session session = new Session();
        session.setEmail(email);
        session.setToken(token);
        sessionMapper.insert(session);

        return token;
    }

    /**
     * 移除token
     *
     * @param email
     */
    public void removeToken(String email) {
        sessionMapper.delete(email);
    }

    /**
     * 获取token
     *
     * @param email
     * @return
     */
    public String getToken(String email) {
        Session session = sessionMapper.getByEmail(email);
        return session == null ? null : session.getToken();
    }
}
