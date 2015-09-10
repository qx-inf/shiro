package com.qx.inf.shiro.seq;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.eis.SessionIdGenerator;

import com.qx.inf.shiro.session.HttpSessionHolder;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月26日 下午6:11:06
 */
public class HttpSessionIdGenerator implements SessionIdGenerator {
    @Override
    public String generateId(Session session) {
        return HttpSessionHolder.getSession().getId();
    }
}