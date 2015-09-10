package com.qx.inf.shiro.principal;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年9月9日 下午2:33:00
 */
public interface PrincipalResolver<T> {
    T resolvePrincipal(Class<T> principalType);
    void syncPrincipal(T principal);
}