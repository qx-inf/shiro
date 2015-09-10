package com.qx.inf.shiro.principal;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.session.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;

/**
 * 身份对象解析.
 * 
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年9月9日 下午3:15:12
 */
public abstract class BasePrincipalResolver<T> implements PrincipalResolver<T> {

    private static final String DOT = ".";

    protected final Logger log = LoggerFactory.getLogger(getClass());
    
    private static final String CLASS_NAME = "class";
    
    private AuthorizingRealm authorizingRealm;

    @Override
    public T resolvePrincipal(Class<T> principalType) {
        T principal = null;
        try {
            principal = BeanUtils.instantiate(principalType);
            principal = setPrincipal(setPrincipal(principal), authorizingRealm.getName());
        } catch (Exception e) {
            log.error("ResolvePrincipal Error.", e);
        }
        return principal;
    }

    protected abstract T setPrincipal(T principal, String realmName);
    
    @Override
    public void syncPrincipal(final T principal) {
        sync(principal);
    }
    
    protected final T setPrincipal(T principal) {
        if (principal == null) {
            return principal;
        }
        Session session = SecurityUtils.getSubject().getSession();
        if (session == null) {
            return principal;
        }
        Class<?> principalType = principal.getClass();
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(principalType);
        for (PropertyDescriptor property : pds) {
            String name = property.getName();
            if (StringUtils.equals(name, CLASS_NAME)) {
                continue;
            }
            Field parameterField = ReflectionUtils.findField(principalType, name);
            SessionKey sessionKey = AnnotationUtils.getAnnotation(parameterField, SessionKey.class);
            if (sessionKey == null) {
                continue;
            }
            String key = sessionKey.key();
            if (StringUtils.isBlank(key)) {
                key = principalType.getName() + DOT + name;
            }
            Object value = session.getAttribute(key);
            if (value == null) {
                continue;
            }
            ReflectionUtils.invokeMethod(property.getWriteMethod(), principal, value);
        }
        return principal;
    }
    
    protected final void sync(final T principal) {
        if (principal == null) {
            return;
        }
        Session session = SecurityUtils.getSubject().getSession();
        if (session == null) {
            return;
        }
        Class<?> principalType = principal.getClass();
        PropertyDescriptor[] pds = BeanUtils.getPropertyDescriptors(principalType);
        for (PropertyDescriptor property : pds) {
            String name = property.getName();
            if (StringUtils.equals(name, CLASS_NAME)) {
                continue;
            }
            Object value = ReflectionUtils.invokeMethod(property.getReadMethod(), principal);
            if (value == null) {
                continue;
            }
            Field parameterField = ReflectionUtils.findField(principalType, name);
            SessionKey sessionKey = AnnotationUtils.getAnnotation(parameterField, SessionKey.class);
            if (sessionKey == null) {
                continue;
            }
            String key = sessionKey.key();
            if (StringUtils.isBlank(key)) {
                key = principalType.getName() + DOT + name;
            }
            session.setAttribute(key, value);
        }
    }

    public void setAuthorizingRealm(AuthorizingRealm authorizingRealm) {
        this.authorizingRealm = authorizingRealm;
    }
}
