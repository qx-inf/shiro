package com.qx.inf.shiro.security;

import java.util.Collection;

import org.apache.shiro.session.Session;

import com.qx.inf.shiro.session.SimpleSession;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月25日 下午4:38:13
 */
public interface SessionRepository {
    String add(SimpleSession session);
    SimpleSession get(String sessionId);
    void remove(SimpleSession session);
    void touch(SimpleSession session);
    Collection<Session> getActiveSessions();
}