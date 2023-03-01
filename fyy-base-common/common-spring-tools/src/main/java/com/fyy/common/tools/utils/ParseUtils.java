package com.fyy.common.tools.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;

import java.util.List;

/**
 * hutool已经有造好的轮子, 当你看到这个注释时, 可以考虑把这个类的调用改成hutool
 *
 * @author hzy
 * @since 2021-05-25
 */
public class ParseUtils {

    public static Long parseLong(Object s) {
        return Convert.toLong(s);
    }

    public static Long parseLong(Object s, Long defaultValue) {
        return Convert.toLong(s, defaultValue);
    }


    public static List<Long> parseLongList(Object s) {
        return CollUtil.toList(Convert.toLongArray(s));
    }

    public static List<String> parseStrList(Object s) {
        return CollUtil.toList(Convert.toStrArray(s));
    }
}