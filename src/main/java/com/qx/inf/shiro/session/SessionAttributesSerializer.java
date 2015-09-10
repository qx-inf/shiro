package com.qx.inf.shiro.session;

import java.io.IOException;
import java.util.Map;

import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.support.DefaultSubjectContext;
import org.apache.shiro.web.util.SavedRequest;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.qx.inf.shiro.attr.AttrObject;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月25日 下午2:38:43
 */
public class SessionAttributesSerializer extends JsonSerializer<Map<Object, AttrObject>> {

    @Override
    public void serialize(final Map<Object, AttrObject> value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
        SessionAttributes sessionAttributes = new SessionAttributes();
        AttrObject shiroSavedRequest = value.get(SessionAttributes.SHIRO_SAVED_REQUEST_KEY);
        if (shiroSavedRequest != null) {
            sessionAttributes.request((SavedRequest) shiroSavedRequest.holder());
        }
        AttrObject pincipalCollection = value.get(DefaultSubjectContext.PRINCIPALS_SESSION_KEY);
        if (pincipalCollection != null) {
            sessionAttributes.principal((SimplePrincipalCollection) pincipalCollection.holder());
        }
        for (Map.Entry<Object, AttrObject> attr : value.entrySet()) {
            Object key = attr.getKey();
            if (SessionAttributes.SHIRO_SAVED_REQUEST_KEY.equals(key)
                    || DefaultSubjectContext.PRINCIPALS_SESSION_KEY.equals(key)) {
                continue;
            }
            sessionAttributes.addAttr(key, attr.getValue());
        }
        gen.writeObject(sessionAttributes);
    }
}
