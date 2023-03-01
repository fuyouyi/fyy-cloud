package com.fyy.common.tools.utils;

import cn.hutool.core.util.StrUtil;

/**
 * 参数处理工具类
 *
 * @author lvbin
 * @since 2019/12/3
 */
public class ParamUtils {

    /**
     * 安全的参数转换，避免转换过程中出现空指针
     *
     * @param val
     * @return
     * @throws NumberFormatException 如果入参不是数值类型
     */
    public static Long parseLong(String val) {
        if (StrUtil.isEmpty(val)) {
            return null;
        }
        return Long.valueOf(val);
    }

    /**
     * 安全的参数转换，避免转换过程中出现空指针
     *
     * @param val
     * @return
     * @throws NumberFormatException 如果入参不是数值类型
     */
    public static Long parseLong(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Long) {
            return (Long) val;
        }
        return parseLong((String) val);
    }

    /**
     * 安全的参数转换，避免转换过程中出现空指针
     *
     * @param val
     * @return
     * @throws NumberFormatException 如果入参不是数值类型
     */
    public static Double parseDouble(String val) {
        if (StrUtil.isEmpty(val)) {
            return null;
        }
        return Double.valueOf(val);
    }

    /**
     * 安全的参数转换，避免转换过程中出现空指针
     *
     * @param val
     * @return
     * @throws NumberFormatException 如果入参不是数值类型
     */
    public static Double parseDouble(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Double) {
            return (Double) val;
        }
        return parseDouble((String) val);
    }

    /**
     * 安全的参数转换，避免转换过程中出现空指针
     *
     * @param val
     * @return
     * @throws NumberFormatException 如果入参不是数值类型
     */
    public static Integer parseInteger(String val) {
        if (StrUtil.isEmpty(val)) {
            return null;
        }
        return Integer.valueOf(val);
    }

    /**
     * 安全的参数转换，避免数值为null导致的空指针异常
     *
     * @param val
     * @return
     * @throws NumberFormatException 如果入参不是数值类型
     */
    public static Integer parseInteger(Object val) {
        if (val == null) {
            return null;
        }
        if (val instanceof Integer) {
            return (Integer) val;
        }
        return parseInteger((String) val);
    }

    /**
     * 格式化浮点数，保留固定小数位
     *
     * @param number
     * @param scale
     * @return
     */
    public static String numberToString(Double number, Integer scale) {
        if (scale == null || scale < 0) {
            throw new IllegalArgumentException("scale must positive");
        }
        if (number == null) {
            throw new IllegalArgumentException("number must not be null");
        }
        return String.format("%." + scale + "f", number);
    }

    /**
     * 格式化浮点数，保留固定小数位
     *
     * @param number
     * @param scale
     * @return
     */
    public static Double formatDouble(Double number, Integer scale) {
        return Double.valueOf(numberToString(number, scale));
    }
}
