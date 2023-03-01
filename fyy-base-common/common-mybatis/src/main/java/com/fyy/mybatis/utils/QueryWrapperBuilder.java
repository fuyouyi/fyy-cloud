package com.fyy.mybatis.utils;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fyy.common.tools.utils.ObjectUtils;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * QueryWrapper 构造器
 * @author lvbin
 * @since 2020/4/3
 */
public class QueryWrapperBuilder<T> {

    private Map<String, Object> params;
    private Map<String, String> eqConditions = new HashMap<>();
    private Map<String, String> neqConditions = new HashMap<>();
    private Map<String, String> likeConditions = new HashMap<>();
    private Map<String, String> inConditions = new HashMap<>();
    private Map<String, String> betweenConditions = new HashMap<>();

    private static final String SPLIT = ",";

    public QueryWrapperBuilder(Map<String, Object> params) {
        this.params = params;
    }

    /**
     * 设置等值查询条件
     * @param columnName 字段名
     * @param propName 参数名
     * @return
     */
    public QueryWrapperBuilder<T> eq(String columnName, String propName) {
        if(StrUtil.isNotBlank(propName) && StrUtil.isNotBlank(columnName)) {
            eqConditions.put(propName, columnName);
        }
        return this;
    }


    /**
     * 设置非等值查询条件
     * @param columnName 字段名
     * @param propName 参数名
     * @return
     */
    public QueryWrapperBuilder<T> ne(String columnName, String propName) {
        if(StrUtil.isNotBlank(propName) && StrUtil.isNotBlank(columnName)) {
            neqConditions.put(propName, columnName);
        }
        return this;
    }



    /**
     * 设置 like 查询条件
     * @param columnName 字段名
     * @param propName 参数名
     * @return
     */
    public QueryWrapperBuilder<T> like(String columnName, String propName) {
        if(StrUtil.isNotBlank(propName) && StrUtil.isNotBlank(columnName)) {
            likeConditions.put(propName, columnName);
        }
        return this;
    }

    /**
     * 设置 in 查询条件
     * @param columnName 字段名
     * @param propName 参数名
     * @return
     */
    public QueryWrapperBuilder<T> in(String columnName, String propName) {
        if(StrUtil.isNotBlank(propName) && StrUtil.isNotBlank(columnName)) {
            inConditions.put(propName, columnName);
        }
        return this;
    }

    /**
     * 设置 between 查询条件
     * @param columnName 字段名
     * @param minPropName 最小值参数名
     * @param maxPropName 最大值参数名
     * @return
     */
    public QueryWrapperBuilder<T> between(String columnName, String minPropName, String maxPropName) {
        if(StrUtil.isNotBlank(columnName) && StrUtil.isNotBlank(minPropName) && StrUtil.isNotBlank(maxPropName)) {
            betweenConditions.put(minPropName + SPLIT + maxPropName, columnName);
        }
        return this;
    }

    public QueryWrapper<T> build() {
        QueryWrapper<T> wrapper = new QueryWrapper<T>();
        // 等值条件
        if(!eqConditions.isEmpty()) {
            eqConditions.forEach((p,f)->{
                Object val = getStr(params, p);
                wrapper.eq(ObjectUtils.isNotEmpty(val), f, val);
            });
        }

        // 非等值条件
        if(!neqConditions.isEmpty()) {
            neqConditions.forEach((p,f)->{
                Object val = getStr(params, p);
                wrapper.ne(ObjectUtils.isNotEmpty(val), f, val);
            });
        }

        // like条件
        if(!likeConditions.isEmpty()) {
            likeConditions.forEach((p,f)->{
                Object val = getStr(params, p);
                wrapper.like(ObjectUtils.isNotEmpty(val), f, val);
            });
        }
        // in条件
        if(!inConditions.isEmpty()) {
            inConditions.forEach((p,f)->{
                Collection c = (Collection) params.get(p);
                wrapper.in(c != null && !c.isEmpty(), f, c);
            });
        }
        // between条件
//        if(!betweenConditions.isEmpty()) {
//            betweenConditions.forEach((p,f)->{
//                String[] propName = p.split(SPLIT);
//                Date minVal = MapUtil.getDate(params, propName[0]);
//                Date maxVal = MapUtil.getDate(params, propName[1]);
//                wrapper.between(minVal!=null && maxVal!=null, f, minVal, maxVal);
//            });
//        }
        if(!betweenConditions.isEmpty()) {
            betweenConditions.forEach((p,f)->{
                String[] propName = p.split(SPLIT);
                Object minVal = getStr(params, propName[0]);
                Object maxVal = getStr(params, propName[1]);
                wrapper.between(ObjectUtils.isNotEmpty(minVal) && ObjectUtils.isNotEmpty(maxVal), f, minVal, maxVal);
            });
        }
        return wrapper;
    }

    private Object getStr(Map<String, Object> params, String key) {
        Object val = params.get(key);
        if(val == null) {
            return "";
        }
        if(val instanceof Date) {
            return DateUtil.format((Date) val, DatePattern.NORM_DATETIME_FORMAT);
        }
        else if( val instanceof Integer || val instanceof Long ){
            return val;
        }
        return MapUtil.getStr(params, key);
    }
}
