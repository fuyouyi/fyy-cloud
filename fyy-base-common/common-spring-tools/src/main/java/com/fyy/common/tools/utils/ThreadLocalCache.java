package com.fyy.common.tools.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * threadlocal 工具类
 * @author huxingwei
 * @since 2022-12-20
 */
public class ThreadLocalCache {

    private final static ThreadLocal<Map<String, Object>> cache;

    static {
        cache = ThreadLocal.withInitial(() -> new HashMap<String, Object>());
    }

    public static void put(String key, Object value) {
        cache.get().put(key, value);
    }

    public static Object get(String key) {
        return cache.get().get(key);
    }
}
