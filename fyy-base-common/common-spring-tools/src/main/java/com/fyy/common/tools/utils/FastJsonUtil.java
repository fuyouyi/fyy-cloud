package com.fyy.common.tools.utils;

import com.alibaba.fastjson2.util.ParameterizedTypeImpl;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fuyouyi
 */
public class FastJsonUtil {

    /**
     * fastJson使用ParameterizedTypeImpl内存泄漏
     * https://www.cnblogs.com/liqipeng/p/11665889.html
     */
    private static final ConcurrentHashMap<String, ParameterizedTypeImpl> parameterizedTypeMap = new ConcurrentHashMap<>();

    public static <T, R> ParameterizedTypeImpl getListTParameterizedType(Class<T> baseResultClass, Class<R> tClass) {
        String key = baseResultClass.getName() + tClass.getName();
        if (parameterizedTypeMap.containsKey(key)) {
            return parameterizedTypeMap.get(key);
        } else {
            ParameterizedTypeImpl inner = new ParameterizedTypeImpl(new Type[]{tClass}, null, List.class);
            ParameterizedTypeImpl outer = new ParameterizedTypeImpl(new Type[]{inner}, null, baseResultClass);
            parameterizedTypeMap.put(key, outer);
            return outer;
        }
    }
}
