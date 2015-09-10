package com.qx.inf.shiro.attr;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.qx.inf.shiro.utils.SerializeUtils;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月26日 下午3:56:24
 */
public class AttrObject implements Serializable {

    private static final long serialVersionUID = 1409849011009586343L;

    private AttrType type;

    private Class<?> parametrizedType;

    private String value;

    private Class<?> keyType;// for map

    private Class<?> valueType;// for map

    private Class<?> elementType;// for collection or array
    
    @JsonIgnore
    private Object valueHolder;

    public AttrObject() {
        super();
    }

    public AttrObject(Object value) {
        this();
        if (value == null) return;
        this.parametrizedType = value.getClass();
        this.valueHolder = value;
        this.value = SerializeUtils.serialize(value);
        init(value);
    }
    
    @JsonIgnore
    private void init(Object value) {
        setType(AttrType.SIMPLE);
        if (Map.class.isAssignableFrom(parametrizedType)) {
            setType(AttrType.MAP);
            for (Map.Entry<?, ?> e : ((Map<?, ?>) value).entrySet()) {
                if (e == null || e.getKey() == null || e.getValue() == null) continue;
                setKeyType(e.getKey().getClass());
                setValueType(e.getValue().getClass());
                return;
            }
            return;
        }
        if (Collection.class.isAssignableFrom(parametrizedType)) {
            setType(AttrType.COLLECTION);
            for (Object obj : (Collection<?>) value) {
                if (obj == null) continue;
                setElementType(obj.getClass());
                return;
            }
            return;
        }
        if (parametrizedType.isArray()) {
            setType(AttrType.ARRAY);
            setElementType((((Object[]) value)[0]).getClass());
            return;
        }
    }

    public AttrType getType() {
        return type;
    }

    public void setType(AttrType type) {
        this.type = type;
    }

    public Class<?> getParametrizedType() {
        return parametrizedType;
    }

    public void setParametrizedType(Class<?> parametrizedType) {
        this.parametrizedType = parametrizedType;
    }
    
    @JsonIgnore
    public boolean isReturnHolder() {
        return null != valueHolder;
    }
    
    @JsonIgnore
    public Object readValueAsType() {
        return AttrUtils.deserializeAttribute(this);
    }
    
    @JsonIgnore
    public Object holder() {
        return this.valueHolder;
    }
    
    @JsonIgnore
    public void holder(Object valueHolder) {
        this.valueHolder = valueHolder;
    }

    public String getValue() {
        return value;
    }
    
    public void setValue(String value) {
        this.value = value;
    }

    public Class<?> getKeyType() {
        return keyType;
    }

    public void setKeyType(Class<?> keyType) {
        this.keyType = keyType;
    }

    public Class<?> getValueType() {
        return valueType;
    }

    public void setValueType(Class<?> valueType) {
        this.valueType = valueType;
    }

    public Class<?> getElementType() {
        return elementType;
    }

    public void setElementType(Class<?> elementType) {
        this.elementType = elementType;
    }
}