package com.qx.inf.shiro.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月25日 下午4:27:05
 */
public class SimpleSessionFactory implements SessionFactory {
    
    @Override
    public Session createSession(SessionContext context) {
        if (context != null) {
            String host = context.getHost();
            if (host != null) {
                return new SimpleSession(host);
            }
        }
        return new SimpleSession();
    }
}