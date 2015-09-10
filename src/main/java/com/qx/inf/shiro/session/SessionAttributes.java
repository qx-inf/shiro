package com.qx.inf.shiro.session;

import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.web.util.SavedRequest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qx.inf.shiro.attr.AttrObject;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月25日 下午3:31:50
 */
public class SessionAttributes {

    public static final String SHIRO_SAVED_REQUEST_KEY = "shiroSavedRequest";

    private String method;
    
    private String queryString;
    
    private String requestURI;
    
    private AttrObject principal;
    
    private String realmName;
    
    private Map<Object, AttrObject> attributes;

    public Map<Object, AttrObject> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<Object, AttrObject> attributes) {
        this.attributes = attributes;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getQueryString() {
        return queryString;
    }

    public void setQueryString(String queryString) {
        this.queryString = queryString;
    }

    public String getRequestURI() {
        return requestURI;
    }

    public void setRequestURI(String requestURI) {
        this.requestURI = requestURI;
    }

    public AttrObject getPrincipal() {
        return principal;
    }

    public void setPrincipal(AttrObject principal) {
        this.principal = principal;
    }

    public String getRealmName() {
        return realmName;
    }

    public void setRealmName(String realmName) {
        this.realmName = realmName;
    }
    
    @JsonIgnore
    public SessionAttributes addAttr(Object key, AttrObject value) {
        if (attributes == null) {
            attributes = new HashMap<Object, AttrObject>();
        }
        attributes.put(key, value);
        return this;
    }

    @JsonIgnore
    public void request(SavedRequest savedRequest) {
        this.method = savedRequest.getMethod();
        this.queryString = savedRequest.getQueryString();
        this.requestURI = savedRequest.getRequestURI();
    }
    
    @JsonIgnore
    public boolean hasRequest() {
        return method != null || queryString != null || requestURI != null;
    }
    
    @JsonIgnore
    public SavedRequest buildRequest() {
        return new SavedRequest(new SimpleHttpRequest(method, queryString, requestURI));
    }

    @JsonIgnore
    public void principal(SimplePrincipalCollection principalCollection) {
        this.principal = new AttrObject(principalCollection.getPrimaryPrincipal());
        this.realmName = principalCollection.getRealmNames().iterator().next();
    }
    
    @JsonIgnore
    public boolean hasPrincipal() {
        return realmName != null;
    }
    
    @JsonIgnore
    public SimplePrincipalCollection buildPrincipal() {
        return new SimplePrincipalCollection(principal.readValueAsType(), realmName);
    }
}