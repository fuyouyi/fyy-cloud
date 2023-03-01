package com.fyy.mybatis.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.fyy.common.tools.utils.ObjectUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * LambdaQueryWrapper 构造器
 *
 * @author fuyouyi
 * @since 2022/12/27
 */
public class LambdaQueryWrapperBuilder<T>{

    private final Map<String, Object> params;
    private final Map<String, SFunction<T, ?>> eqConditions = new HashMap<>();
    private final Map<String, SFunction<T, ?>> neqConditions = new HashMap<>();
    private final Map<String, SFunction<T, ?>> likeConditions = new HashMap<>();
    private final Map<String, SFunction<T, ?>> inConditions = new HashMap<>();
    private final Map<String, SFunction<T, ?>> geConditions = new HashMap<>();
    private final Map<String, SFunction<T, ?>> leConditions = new HashMap<>();
    private final Map<String, SFunction<T, ?>> betweenConditions = new HashMap<>();

    private static final String SPLIT = ",";

    public LambdaQueryWrapperBuilder(Map<String, Object> params) {
        this.params = params;
    }

    /**
     * 设置等值查询条件
     *
     * @param columnName 字段名
     * @param propName   参数名
     * @return
     */
    public LambdaQueryWrapperBuilder<T> eq(SFunction<T, ?> columnName, String propName) {
        if (StrUtil.isNotBlank(propName)) {
            eqConditions.put(propName, columnName);
        }
        return this;
    }

    /**
     * 设置非等值查询条件
     *
     * @param columnName 字段名
     * @param propName   参数名
     * @return
     */
    public LambdaQueryWrapperBuilder<T> ne(SFunction<T, ?> columnName, String propName) {
        if (StrUtil.isNotBlank(propName)) {
            neqConditions.put(propName, columnName);
        }
        return this;
    }

    /**
     * 设置ge查询条件
     *
     * @param columnName 字段名
     * @param propName   参数名
     * @return
     */
    public LambdaQueryWrapperBuilder<T> ge(SFunction<T, ?> columnName, String propName) {
        if (StrUtil.isNotBlank(propName)) {
            geConditions.put(propName, columnName);
        }
        return this;
    }

    /**
     * 设置le查询条件
     *
     * @param columnName 字段名
     * @param propName   参数名
     * @return
     */
    public LambdaQueryWrapperBuilder<T> le(SFunction<T, ?> columnName, String propName) {
        if (StrUtil.isNotBlank(propName)) {
            leConditions.put(propName, columnName);
        }
        return this;
    }


    /**
     * 设置 like 查询条件
     *
     * @param columnName 字段名
     * @param propName   参数名
     * @return
     */
    public LambdaQueryWrapperBuilder<T> like(SFunction<T, ?> columnName, String propName) {
        if (StrUtil.isNotBlank(propName)) {
            likeConditions.put(propName, columnName);
        }
        return this;
    }

    /**
     * 设置 in 查询条件
     *
     * @param columnName 字段名
     * @param propName   参数名
     * @return
     */
    public LambdaQueryWrapperBuilder<T> in(SFunction<T, ?> columnName, String propName) {
        if (StrUtil.isNotBlank(propName)) {
            inConditions.put(propName, columnName);
        }
        return this;
    }

    /**
     * 设置 between 查询条件
     *
     * @param columnName  字段名
     * @param minPropName 最小值参数名
     * @param maxPropName 最大值参数名
     * @return
     */
    public LambdaQueryWrapperBuilder<T> between(SFunction<T, ?> columnName, String minPropName, String maxPropName) {
        if (StrUtil.isNotBlank(minPropName) && StrUtil.isNotBlank(maxPropName)) {
            betweenConditions.put(minPropName + SPLIT + maxPropName, columnName);
        }
        return this;
    }


    public static <T> LambdaQueryWrapperBuilder<T> builder(Class<T> tClass, Map<String, Object> params){
        return new LambdaQueryWrapperBuilder<T>(params);
    }

    public LambdaQueryWrapper<T> build() {
        LambdaQueryWrapper<T> wrapper = new LambdaQueryWrapper<T>();
        // 等值条件
        if (MapUtil.isNotEmpty(eqConditions)) {
            eqConditions.forEach((p, f) -> {
                Object val = getStr(params, p);
                wrapper.eq(ObjectUtils.isNotEmpty(val), f, val);
            });
        }

        // 非等值条件
        if (MapUtil.isNotEmpty(neqConditions)) {
            neqConditions.forEach((p, f) -> {
                Object val = getStr(params, p);
                wrapper.ne(ObjectUtils.isNotEmpty(val), f, val);
            });
        }

        // ge条件
        if (MapUtil.isNotEmpty(geConditions)) {
            geConditions.forEach((p, f) -> {
                Object val = getStr(params, p);
                wrapper.ge(ObjectUtils.isNotEmpty(val), f, val);
            });
        }

        // le条件
        if (MapUtil.isNotEmpty(leConditions)) {
            leConditions.forEach((p, f) -> {
                Object val = getStr(params, p);
                wrapper.le(ObjectUtils.isNotEmpty(val), f, val);
            });
        }

        // like条件
        if (MapUtil.isNotEmpty(likeConditions)) {
            likeConditions.forEach((p, f) -> {
                Object val = getStr(params, p);
                wrapper.like(ObjectUtils.isNotEmpty(val), f, val);
            });
        }
        // in条件
        if (MapUtil.isNotEmpty(inConditions)) {
            inConditions.forEach((p, f) -> {
                Collection<?> c = (Collection<?>) params.get(p);
                wrapper.in(CollUtil.isNotEmpty(c), f, c);
            });
        }
        // between条件
        if (MapUtil.isNotEmpty(betweenConditions)) {
            betweenConditions.forEach((p, f) -> {
                String[] propName = p.split(SPLIT);
                Object minVal = getStr(params, propName[0]);
                Object maxVal = getStr(params, propName[1]);
                wrapper.between(ObjectUtils.isNotEmpty(minVal) && ObjectUtils.isNotEmpty(maxVal), f, minVal, maxVal);
            });
        }
        return wrapper;
    }

    public static Object getStr(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if (val == null) {
            return "";
        }
        if (val instanceof Date) {
            return DateUtil.format((Date) val, DatePattern.NORM_DATETIME_FORMAT);
        } else if (val instanceof Integer || val instanceof Long) {
            return val;
        }
        return MapUtil.getStr(params, key);
    }

    public static void main(String[] args) {
        Map<String, Object> params = new HashMap<>();

        params.put("id", 1L);
        Object val = getStr(params, "id");
        System.out.println(ObjectUtils.isNotEmpty(val) + " " + val);

        params.put("startTime", new Date());
        Object val2 = getStr(params, "startTime");
        System.out.println(ObjectUtils.isNotEmpty(val2) + " " + val2);

        Object val3 = getStr(params, "endTime");
        System.out.println(ObjectUtils.isNotEmpty(val3) + " " + val3);
    }
}
