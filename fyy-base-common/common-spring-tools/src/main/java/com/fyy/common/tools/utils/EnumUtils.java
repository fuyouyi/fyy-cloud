package com.fyy.common.tools.utils;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.util.EnumUtil;

import java.util.LinkedHashMap;
import java.util.function.Function;

/**
 * @author fyy
 * @since 2023/02/28
 */
public class EnumUtils {

    private final static Long ONE_HOUR = 60 * 60 * 1000L;

    private final static LRUCache<String, LinkedHashMap<String, ?>> cacheMap = CacheUtil.newLRUCache(50, ONE_HOUR);

    /**
     * 【不会报错】 查不到返回null
     *
     * @param enumClass 枚举类
     * @param enumName  枚举名称
     * @return 枚举名称 对应的 枚举值
     */
    public static <E extends Enum<E>> E get(final Class<E> enumClass, String enumName) {
        if (!cacheMap.containsKey(enumClass.getName())) {
            synchronized (cacheMap) {
                if (!cacheMap.containsKey(enumClass.getName())) {
                    cacheMap.put(enumClass.getName(), EnumUtil.getEnumMap(enumClass));
                }
            }
        }
        if (enumName == null) {
            return null;
        } else {
            return (E) (cacheMap.get(enumClass.getName()).get(enumName));
        }
    }

    /**
     * 【不会报错】 查不到返回null
     *
     * @param enumClass 枚举类
     * @param enumName  枚举名称
     * @param func      枚举类的get方法
     * @return 枚举名称 对应枚举值的 属性
     */
    public static <E extends Enum<E>, R> R getField(final Class<E> enumClass, String enumName, Function<E, R> func) {
        E currentE = get(enumClass, enumName);
        if (currentE == null) {
            return null;
        }
        return func.apply(currentE);
    }

    /**
     * 【不会报错】 查不到返回defaultValue
     *
     * @param enumClass 枚举类
     * @param enumName  枚举名称
     * @param func      枚举类的get方法
     * @return 枚举名称 对应枚举值的 属性
     */
    public static <E extends Enum<E>, R> R getField(final Class<E> enumClass, String enumName, Function<E, R> func, R defaultValue) {
        E currentE = get(enumClass, enumName);
        if (currentE == null) {
            return defaultValue;
        }
        return func.apply(currentE);
    }

    /**
     * 【不会报错】 查询String类型的值, 查不到返回enumName（该方法用于大多数展示逻辑，在拿到枚举以外的值时，保证desc能用code将就展示）
     *
     * @param enumClass 枚举类
     * @param enumName  枚举名称
     * @param func      枚举类的get方法
     * @return 枚举名称 对应枚举值的 属性
     */
    public static <E extends Enum<E>> String getStrField(final Class<E> enumClass, String enumName, Function<E, String> func) {
        E currentE = get(enumClass, enumName);
        if (currentE == null) {
            return enumName;
        }
        return func.apply(currentE);
    }
}
