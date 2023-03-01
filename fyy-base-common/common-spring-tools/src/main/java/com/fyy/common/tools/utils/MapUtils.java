package com.fyy.common.tools.utils;

import cn.hutool.core.map.MapBuilder;
import cn.hutool.core.map.MapUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 简化Hutool的 mapUtils用法
 *
 * @author fuyouyi
 */
public class MapUtils {

    public static MapBuilder<String, Object> builder() {
        return MapUtil.builder(new HashMap<>());
    }

    public static MapBuilder<String, Object> builder(String key, Object value) {
        return builder().put(key, value);
    }

    public static void main(String[] args) {
        Map<String, Object> map = MapUtils.builder("id", 132L).build();
    }
}
