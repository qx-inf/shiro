package com.qx.inf.shiro.security;

import org.apache.shiro.mgt.DefaultSubjectDAO;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年9月6日 下午7:57:25
 */
public class SimpleWebSecurityManager extends DefaultWebSecurityManager {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    public SimpleWebSecurityManager() {
        super();
        ((DefaultSubjectDAO) subjectDAO).setSessionStorageEvaluator(new SimpleSessionStorageEvaluator());
    }
}