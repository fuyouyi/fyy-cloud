package com.fyy.common.tools.utils;

import cn.hutool.cache.CacheUtil;
import cn.hutool.cache.impl.LRUCache;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.func.Func1;
import cn.hutool.core.lang.func.LambdaUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.fyy.common.tools.global.ViewEnumDesc;
import com.fyy.common.tools.global.page.PageData;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fuyouyi
 * @since 2022-09-08
 */
public class ResultEnumToDescUtil {

    private final static Long ONE_DAY = 24 * 60 * 60 * 1000L;

    private final static LRUCache<String, HashMap<Method, Class>> cacheMap = CacheUtil.newLRUCache(50, ONE_DAY);

    public static Object beforeBodyWrite(Object obj) {
        Object result;
        if (obj instanceof Collection) {
            result = ((Collection) obj).stream().map(ResultEnumToDescUtil::addEnumDesc).collect(Collectors.toList());
        } else if (obj instanceof PageData) {
            ((PageData) obj).setList(Collections.singletonList(((PageData) obj).getList().stream().map(ResultEnumToDescUtil::addEnumDesc).collect(Collectors.toList())));
            result = obj;
        } else {
            result = addEnumDesc(obj);
        }
        return result;
    }

    /**
     * 【方式1】 根据 ViewEnumDesc 注解, 增加desc
     *
     * @see ViewEnumDesc
     */
    public static Object addEnumDesc(Object obj) {
        HashMap<Method, Class> methodClassHashMap;
        try {
            methodClassHashMap = getMap(obj);
        } catch (Exception e) {
            methodClassHashMap = new HashMap<>();
        }

        if (CollUtil.isEmpty(methodClassHashMap)) {
            return obj;
        }
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(obj));
        methodClassHashMap.forEach((method, enumClass) -> {
            // desc字段名称 例如 monitorStatus -> monitorStatusDesc
            try {
                String key = StrUtil.lowerFirst(method.getName().replaceAll("get", "")) + "Desc";
                // desc字段值
                String enumName = (String) method.invoke(obj);
                Method getDescMethod = enumClass.getMethod("getDesc");
                String desc = (String) getDescMethod.invoke(EnumUtils.get(enumClass, enumName));
                jsonObject.put(key, desc);
            } catch (Exception e) {
            }
        });

        return jsonObject;
    }

    /**
     * 【方式二】通过addFun的形式, 将对象添加枚举的desc (注意, 需要调用build()方法生成)
     *
     * @see ConvertBuilder
     */
    public static ConvertBuilder builder(Object o) {
        ConvertBuilder convertBuilder = new ConvertBuilder();
        convertBuilder.setObj(o);
        convertBuilder.setList(new ArrayList<>());
        return convertBuilder;
    }

    /**
     * 【不会报错】 查不到返回空
     *
     * @param originResultObj result对象
     * @return 带有ViewEnumDesc注解的方法, 注解对应的枚举类
     */
    private static HashMap<Method, Class> getMap(Object originResultObj) {
        Class resultClass = originResultObj.getClass();
        if (!cacheMap.containsKey(resultClass.getName())) {
            synchronized (cacheMap) {
                if (!cacheMap.containsKey(resultClass.getName())) {
                    HashMap<Method, Class> enumFieldMethodMap = new HashMap<>();
                    Field[] fields = resultClass.getDeclaredFields();
                    for (Field field : fields) {
                        try {
                            ViewEnumDesc viewEnumDesc = field.getAnnotation(ViewEnumDesc.class);
                            if (viewEnumDesc != null) {
                                Method enumFieldMethod = resultClass.getMethod("get" + StrUtil.upperFirst(field.getName()));
                                enumFieldMethodMap.put(enumFieldMethod, viewEnumDesc.enumClass());
                            }
                        } catch (Exception ignore) {
                        }
                    }
                    cacheMap.put(resultClass.getName(), enumFieldMethodMap);
                }
            }
        }
        return cacheMap.get(resultClass.getName());
    }

    @Getter
    @Setter
    public static class ConvertBuilder {

        private Object obj;

        private List<FunAndEnum> list;

        public <T> ConvertBuilder addFun(Func1<T, ?> func1, Class enumClass) {
            list.add(new FunAndEnum(func1, enumClass));
            return this;
        }

        // 将对象转化成JSONObject, 并添加枚举的Desc
        public JSONObject build() {
            JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(obj));
            list.forEach(funAndEnum -> {
                // desc字段名称 例如 monitorStatus -> monitorStatusDesc
                String key = LambdaUtil.getFieldName(funAndEnum.getFunc1()) + "Desc";
                // desc字段值
                String desc = getObjEnumDesc(obj, funAndEnum.getFunc1(), funAndEnum.getEnumClass());
                jsonObject.put(key, desc);
            });
            return jsonObject;
        }

        private static <T> String getObjEnumDesc(Object obj, Func1<T, ?> func1, Class enumClass) {
            String enumName = "";
            try {
                SerializedLambda serializedLambda = LambdaUtil.resolve(func1);
                Method method = obj.getClass().getMethod(serializedLambda.getImplMethodName());
                enumName = (String) method.invoke(obj);
                Method getDescMethod = enumClass.getMethod("getDesc");
                return (String) getDescMethod.invoke(EnumUtils.get(enumClass, enumName));
            } catch (Exception ignore) {
                return enumName;
            }
        }

        @Data
        @AllArgsConstructor
        private static class FunAndEnum {

            private Func1<?, ?> func1;

            private Class<?> enumClass;
        }
    }
}
