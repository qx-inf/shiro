package com.qx.inf.shiro.session;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.shiro.subject.support.DefaultSubjectContext;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.qx.inf.shiro.attr.AttrObject;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月25日 下午3:36:57
 */
public class SessionAttributesDeserializer extends JsonDeserializer<Map<Object, AttrObject>> {

    @Override
    public Map<Object, AttrObject> deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        SessionAttributes attrObject = p.readValueAs(SessionAttributes.class);
        Map<Object, AttrObject> sessionAttributes = buildSessionAttributes(attrObject);
        return sessionAttributes;
    }

    private Map<Object, AttrObject> buildSessionAttributes(SessionAttributes attributes) {
        Map<Object, AttrObject> sessionAttributes = new HashMap<Object, AttrObject>();
        if (attributes.hasRequest()) {
            AttrObject savedRequest = new AttrObject(true);
            savedRequest.holder(attributes.buildRequest());
            sessionAttributes.put(SessionAttributes.SHIRO_SAVED_REQUEST_KEY, savedRequest);
        }
        if (attributes.hasPrincipal()) {
            AttrObject principals = new AttrObject(true);
            principals.holder(attributes.buildPrincipal());
            sessionAttributes.put(DefaultSubjectContext.PRINCIPALS_SESSION_KEY, principals);
        }
        sessionAttributes.putAll(attributes.getAttributes());
        return sessionAttributes;
    }
    
    
}
