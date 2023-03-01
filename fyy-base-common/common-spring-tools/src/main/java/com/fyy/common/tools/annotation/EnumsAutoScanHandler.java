package com.fyy.common.tools.annotation;

import cn.hutool.core.collection.CollUtil;
import com.alibaba.fastjson2.JSONObject;
import org.reflections.Reflections;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class EnumsAutoScanHandler {

    /**
     * 获取自定义注解@ActionController扫描到的菜单集合
     */
    public static List<EnumsResultDTO> getScanMenus(String packageName) {
        List<EnumsResultDTO> resultDTOList = new ArrayList<>();
        // 要扫描的包
        Reflections f = new Reflections(packageName);
        // 获取扫描到的标记注解的集合
        Set<Class<?>> set = f.getTypesAnnotatedWith(EnumsAutoScan.class);
        for (Class<?> c : set) {
            EnumsResultDTO enumsResultDTO = new EnumsResultDTO();
            // 获取名称
            JSONObject jsonObject = new JSONObject();
            enumsResultDTO.setEnumName(c.getSimpleName());
            enumsResultDTO.setEntryMap(jsonObject);
            try {
                // 获取方法
                Method values = c.getMethod("values");
                Method name = c.getMethod("name");
                Method getDesc = c.getMethod("getDesc");

                Object objectValues = values.invoke(null, null);
                Collection<Object> list = CollUtil.addAll(new ArrayList<>(), objectValues);
                for (Object o : list) {
                    String key = "", value = null;
                    try {
                        key = (String) name.invoke(o, null);
                    } catch (Exception ignored) {
                    }
                    try {
                        value = (String) getDesc.invoke(o, null);
                    } catch (Exception ignored) {
                    }
                    jsonObject.put(key, value);
                }
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
            resultDTOList.add(enumsResultDTO);
        }
        return resultDTOList;
    }
}
