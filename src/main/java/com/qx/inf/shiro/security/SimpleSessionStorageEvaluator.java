package com.qx.inf.shiro.security;

import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.subject.Subject;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年9月6日 下午7:58:59
 */
public class SimpleSessionStorageEvaluator implements SessionStorageEvaluator {

    @Override
    public boolean isSessionStorageEnabled(Subject subject) {
        return true;
    }
}