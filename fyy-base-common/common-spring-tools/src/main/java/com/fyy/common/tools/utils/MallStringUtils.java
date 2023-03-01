package com.fyy.common.tools.utils;

import java.util.Arrays;
import java.util.List;

/**
 * Created by yz on 2019/9/27.
 */
public class MallStringUtils {

    public static String concat(List<String> list) {
        return concat(list.toArray(new String[]{}));
    }

    /**
     * 字符串拼接
     *
     * @param str 待拼接的字符串
     * @return
     */
    public static String concat(String... str) {
        if (str == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        Arrays.stream(str).filter(e -> e != null && e.trim().length() > 0).forEach(e -> {
            builder.append(e).append(",");
        });
        if (builder.length() > 1) {
            return builder.substring(0, builder.length() - 1);
        } else {
            return "";
        }
    }

    /**
     * 向左补齐，例如leftPadding(13,0,4)，输出结果为"0013"
     *
     * @param val        输入数值
     * @param paddingVal 用来填补的数值
     * @param digit      位数
     * @return
     */
    public static String leftPadding(Integer val, Integer paddingVal, Integer digit) {
        if (val == null) {
            throw new IllegalArgumentException("val should not be null");
        }
        if (paddingVal == null) {
            throw new IllegalArgumentException("paddingVal should not be null");
        }
        return String.format("%" + paddingVal + digit + "d", val);
    }

    /**
     * 向左补齐，例如leftPadding(13,0,4)，输出结果为"0013"
     *
     * @param val        输入数值
     * @param paddingVal 用来填补的数值
     * @param digit      位数
     * @return
     */
    public static String leftPadding(Long val, Integer paddingVal, Integer digit) {
        if (val == null) {
            throw new IllegalArgumentException("val should not be null");
        }
        if (paddingVal == null) {
            throw new IllegalArgumentException("paddingVal should not be null");
        }
        return String.format("%" + paddingVal + digit + "d", val);
    }

    public static void main(String[] args) {
        System.out.println(leftPadding(13, 0, 4));
    }
}
