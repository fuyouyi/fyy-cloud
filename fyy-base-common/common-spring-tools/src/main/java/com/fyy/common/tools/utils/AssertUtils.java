package com.fyy.common.tools.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.fyy.common.tools.exception.ErrorCode;
import com.fyy.common.tools.exception.RenException;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 校验工具类
 *
 * @author carl
 * @since 1.0.0
 */
public class AssertUtils {

    public static void isBlank(String str, String... params) {
        if (StrUtil.isBlank(str)) {
            throw new RenException(ErrorCode.NOT_NULL.getCode(), Arrays.toString(params));
        }
    }

    public static void isNull(Object object, String... params) {
        if (object == null) {
            throw new RenException(ErrorCode.NOT_NULL.getCode(), Arrays.toString(params));
        }
    }

    public static void isArrayEmpty(Object[] array, String... params) {
        if (ArrayUtil.isEmpty(array)) {
            throw new RenException(ErrorCode.NOT_NULL.getCode(), Arrays.toString(params));
        }
    }

    public static void isListEmpty(List<?> list, String... params) {
        if (CollUtil.isEmpty(list)) {
            throw new RenException(ErrorCode.NOT_NULL.getCode(), Arrays.toString(params));
        }
    }

    public static void isEmpty(Collection<?> col, String... params) {
        isEmpty(col, ErrorCode.NOT_NULL.getCode(), params);
    }

    public static void isEmpty(Collection<?> col, Integer code, String... params) {
        if (code == null) {
            throw new RenException(ErrorCode.NOT_NULL.getCode());
        }

        if (CollUtil.isEmpty(col)) {
            throw new RenException(code, Arrays.toString(params));
        }
    }

    public static void isMapEmpty(Map map, String... params) {
        isMapEmpty(map, ErrorCode.NOT_NULL.getCode(), params);
    }

    public static void isMapEmpty(Map map, Integer code, String... params) {
        if (code == null) {
            throw new RenException(ErrorCode.NOT_NULL.getCode());
        }

        if (MapUtil.isEmpty(map)) {
            throw new RenException(code, Arrays.toString(params));
        }
    }

    public static void isTrue(boolean b, String msg) {
        if (b) {
            throw new RenException(msg);
        }
    }
}