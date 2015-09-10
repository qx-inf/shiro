package com.qx.inf.shiro.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author 刘飞 E-mail:liufei_it@126.com
 *
 * @version 1.0.0
 * @since 2015年8月25日 下午4:47:57
 */
public final class SerializeUtils {

    private static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setDateFormat(new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS));
    }

    public static String serialize(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to convert object to json", e);
        }
    }

    public static <T> T deserialize(String json, Class<T> targetType) {
        try {
            return objectMapper.readValue(json, targetType);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to convert json to object", e);
        }
    }
    
    public static <T> T deserialize(String json, JavaType targetType) {
        try {
            return objectMapper.readValue(json, targetType);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to convert json to object", e);
        }
    }
    
    @SuppressWarnings("deprecation")
    public static JavaType getType(Class<?> parametrized, Class<?>... parameterClasses) {     
        return objectMapper.getTypeFactory().constructParametricType(parametrized, parameterClasses);     
    }
}