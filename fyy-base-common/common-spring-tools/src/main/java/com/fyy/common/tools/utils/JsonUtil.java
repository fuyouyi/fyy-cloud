package com.fyy.common.tools.utils;

import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.type.TypeParser;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.GsonBuilder;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Json Utils.
 */
@Slf4j
public class JsonUtil {

    private static TypeParser typeParser;
    private static ObjectMapper objectMapper;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        //字段为NULL的时候不会列入
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        typeParser = new TypeParser(objectMapper.getTypeFactory());
    }

    private static GsonBuilder gsonBuilder = new GsonBuilder();

    public static String toJsonWithUnderscores(Object obj) {
        gsonBuilder.setPrettyPrinting();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
        return gsonBuilder.create().toJson(obj);
    }

    public static String toCanonical(Type type) {
        TypeFactory typeFactory = objectMapper.getTypeFactory();
        return typeFactory.constructType(type).toCanonical();
    }

    public static Class[] toRawType(String javaTypeListJson) {
        if (null == javaTypeListJson) {
            return null;
        }
        List<String> javaTypeStrList = toList(javaTypeListJson, String.class);
        Class[] types = new Class[javaTypeStrList.size()];
        for (int i = 0; i < javaTypeStrList.size(); i++) {
            JavaType javaType = typeParser.parse(javaTypeStrList.get(i));
            types[i] = javaType.getRawClass();
        }
        return types;
    }

    public static <T> T toBeanWithCanonical(String source, String javaTypeStr) {
        if (null == source || "null".equals(source)) {
            return null;
        }
        try {
            JavaType javaType = typeParser.parse(javaTypeStr);
            return objectMapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new RuntimeException("json to bean error!~", e);
        }
    }

    public static <T> String toJson(T t) {
        try {
            return objectMapper.writeValueAsString(t);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("bean to json error!~", e);
        }
    }

    public static <T> T toBean(String source, Class<T> clazz) {
        try {
            return objectMapper.readValue(source, clazz);
        } catch (IOException e) {
            throw new RuntimeException("json to bean error!~", e);
        }
    }

    public static <K, T> K toBean(String json, Class<K> kclazz, Class<T> tclazz) {
        if (StrUtil.isEmpty(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(kclazz, tclazz));
        } catch (IOException e) {
            log.error("parse json:{} to {} error!", json, tclazz);
            throw new RuntimeException(e);
        }
    }

    /**
     * resultVo泛型解析
     *
     * @param source
     * @param genericClazz
     * @param dataClazz
     * @return
     * @author fuyouyi
     */
    public static <T> T toGenericBean(String source, Class<T> genericClazz, Class dataClazz) {
        try {
            JavaType type = objectMapper.getTypeFactory().constructParametricType(genericClazz, dataClazz);
            return (T) objectMapper.readValue(source, type);
        } catch (IOException e) {
            throw new RuntimeException("json to GenericBean error!~", e);
        }
    }

    /**
     * resultVo泛型解析---list
     *
     * @param source
     * @param genericClazz
     * @param dataClazz
     * @return
     * @author fuyouyi
     */
    public static <T> T toGenericBeanList(String source, Class genericClazz, Class dataClazz) {
        try {
            JavaType userType = objectMapper.getTypeFactory().constructParametrizedType(ArrayList.class, ArrayList.class, dataClazz);
            JavaType javaType = objectMapper.getTypeFactory().constructParametrizedType(genericClazz, genericClazz, userType);
            return objectMapper.readValue(source, javaType);
        } catch (IOException e) {
            throw new RuntimeException("json to GenericBeanList error!~", e);
        }
    }

    public static <T> T[] toArray(String json, Class<T> clazz) {
        if (StrUtil.isEmpty(json)) {
            return null;
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructArrayType(clazz));
        } catch (IOException e) {
            throw new RuntimeException("json to bean error!~", e);
        }
    }

    public static <T> List<T> toList(String json, Class<T> clazz) {
        if (StrUtil.isEmpty(json)) {
            return new ArrayList<>();
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(List.class, clazz));
        } catch (IOException e) {
            log.error("parse json:{} to {} error!", json, clazz.getClass().getName());
            throw new RuntimeException(e);
        }
    }

    public static <T> Collection<T> toCollection(String json, Class collectionClazz, Class<T> clazz) {
        if (StrUtil.isEmpty(json)) {
            return null;
        }
        if (collectionClazz.isAssignableFrom(Collection.class)) {
            throw new IllegalArgumentException("not a collection class");
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(collectionClazz, clazz));
        } catch (IOException e) {
            log.error("pase json error! , " + json);
            throw new RuntimeException(e);
        }
    }

    public static <K, V> Map<K, V> toMap(String json, Class mapClazz, Class<K> keyClass, Class<V> valueClass) {
        if (StrUtil.isEmpty(json)) {
            return new HashMap<>();
        }
        if (mapClazz.isAssignableFrom(Map.class)) {
            throw new IllegalArgumentException("not a map class");
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(mapClazz, keyClass, valueClass));
        } catch (IOException e) {
            log.error("pase json error! , " + json);
            throw new RuntimeException(e);
        }
    }

    public static <K, V> Map<K, V> toMap(String json, Class<K> keyClass, Class<V> valueClass) {
        if (StrUtil.isEmpty(json)) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(Map.class, keyClass, valueClass));
        } catch (IOException e) {
            log.error("pase json error! , " + json);
            throw new RuntimeException(e);
        }
    }

    public static <T> Set<T> toSet(String json, Class<T> clazz) {
        if (StrUtil.isEmpty(json)) {
            return new HashSet<>();
        }
        try {
            return objectMapper.readValue(json, objectMapper.getTypeFactory().constructParametricType(Set.class, clazz));
        } catch (IOException e) {
            log.error("pase json error! , " + json);
            throw new RuntimeException(e);
        }
    }
}
