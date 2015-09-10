package com.qx.inf.shiro.session;

import javax.servlet.http.HttpSession;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月26日 下午6:12:24
 */
public class HttpSessionHolder {

    private static class LocalHttpSessionHolder {
        private static ThreadLocal<HttpSession> LOCAL_SESSION = new ThreadLocal<HttpSession>();
    }
    
    public static void holder(HttpSession session) {
        LocalHttpSessionHolder.LOCAL_SESSION.set(session);
    }
    
    public static HttpSession getSession() {
        return LocalHttpSessionHolder.LOCAL_SESSION.get();
    }
}