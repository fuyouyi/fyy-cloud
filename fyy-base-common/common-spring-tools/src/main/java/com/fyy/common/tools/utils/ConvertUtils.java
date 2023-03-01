package com.fyy.common.tools.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.util.*;

/**
 * 转换工具类
 *
 * @author carl
 * @since 1.0.0
 */
@Slf4j
public class ConvertUtils extends Convert {

    public static <T> T sourceToTarget(Object source, Class<T> target) {
        if (source == null) {
            return null;
        }
        T targetObject = null;
        try {
            targetObject = target.newInstance();
            BeanUtils.copyProperties(source, targetObject);
        } catch (Exception e) {
            log.error("convert error ", e);
        }

        return targetObject;
    }

    public static <T> List<T> sourceToTarget(Collection<?> sourceList, Class<T> target) {
        if (sourceList == null) {
            return null;
        }

        List targetList = new ArrayList<>(sourceList.size());
        try {
            for (Object source : sourceList) {
                T targetObject = target.newInstance();
                BeanUtils.copyProperties(source, targetObject);
                targetList.add(targetObject);
            }
        } catch (Exception e) {
            log.error("convert error ", e);
        }

        return targetList;
    }

    public static <T> List<T> sourceToTargetList(Collection<?> sourceList, Class<T> target) {
        if (sourceList == null) {
            return null;
        }

        List targetList = new ArrayList<>(sourceList.size());
        try {
            for (Object source : sourceList) {
                T targetObject = target.newInstance();
                targetObject = BeanUtil.mapToBean((Map<?, ?>) source, target, true);
                // BeanUtils.copyProperties(source, targetObject);
                targetList.add(targetObject);
            }
        } catch (Exception e) {
            log.error("convert error ", e);
        }

        return targetList;
    }

    public static String[] longToString(Long longArray[]) {
        if (longArray == null || longArray.length < 1) {
            return null;
        }
        String stringArray[] = new String[longArray.length];
        for (int i = 0; i < stringArray.length; i++) {
            try {
                stringArray[i] = String.valueOf(longArray[i]);
            } catch (NumberFormatException e) {
                stringArray[i] = null;
                continue;
            }
        }
        return stringArray;
    }

    public static String[] longToString(List<Long> longList) {
        return longToString(longList.toArray(new Long[0]));
    }

    public static Long[] stringToLong(String[] strs) {
        if (strs == null || strs.length < 1) {
            return null;
        }
        Long[] longArr = new Long[strs.length];
        for (int i = 0; i < strs.length; i++) {
            longArr[i] = Long.valueOf(strs[i]);
        }
        return longArr;
    }

    public static List<Long> stringToList(String[] strs) {
        if (strs == null || strs.length < 1) {
            return null;
        }

        List<String> list = Arrays.asList(strs);
        List<Long> rs = new ArrayList<>(list.size());
        for (String str : list) {
            rs.add(Long.valueOf(str));
        }
        return rs;
    }

    /**
     * 把一个字符串转换成bean对象
     *
     * @param str
     * @param <T>
     * @return
     */
    public static <T> T stringToBean(String str, Class<T> clazz) {
        if (str == null || str.length() <= 0 || clazz == null) {
            return null;
        }
        if (clazz == int.class || clazz == Integer.class) {
            return (T) Integer.valueOf(str);
        } else if (clazz == String.class) {
            return (T) str;
        } else if (clazz == long.class || clazz == Long.class) {
            return (T) Long.valueOf(str);
        } else {
            return JSON.toJavaObject(JSON.parseObject(str), clazz);
        }
    }

    /**
     * 将任意类型转换成字符串
     *
     * @param value
     * @param <T>
     * @return
     */
    public static <T> String beanToString(T value) {
        Class<?> clazz = value.getClass();
        if (clazz == int.class || clazz == Integer.class) {
            return value + "";
        } else if (clazz == String.class) {
            return (String) value;
        } else if (clazz == long.class || clazz == Long.class) {
            return value + "";
        } else {
            return JSON.toJSONString(value);
        }

    }

    /**
     * 转换为String map
     *
     * @param map
     * @return
     */
    public static Map<String, String> parseToStringMap(Map<String, Object> map) {
        if (MapUtil.isEmpty(map)) {
            return new HashMap<>();
        }
        Map<String, String> strMap = new HashMap<>();
        map.forEach((key, value) -> {
            if (null != value) {
                strMap.put(key, String.valueOf(value));
            }
        });
        return strMap;
    }
}