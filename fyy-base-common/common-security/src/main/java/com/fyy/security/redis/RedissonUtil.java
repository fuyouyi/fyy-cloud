package com.fyy.security.redis;

import cn.hutool.core.map.MapUtil;
import com.fyy.common.tools.config.AutowireInitializingStaticUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Redisson框架
 * <p>
 * Available Spring Beans:
 * RedissonClient
 * RedissonRxClient
 * RedissonReactiveClient
 * RedisTemplate
 * ReactiveRedisTemplate
 */
@Service
@Slf4j
public class RedissonUtil extends AutowireInitializingStaticUtil {

    @Autowired
    protected static RedissonClient redissonClient;

    private static final ConcurrentHashMap<String, RRateLimiter> limiterMap = new ConcurrentHashMap<>();

    /**
     * 限流器额度Map，可以针对每个key都自定义限流。
     * com.fyy.common.tools.redis.RedissonUtil#getLimit(java.lang.String, java.lang.String)
     * 这里会进行适配
     */
    private static Map<String, Map<String, Integer>> limitMap;

    static {
        limitMap = new HashMap<>();
        Map<String, Integer> qianchuanLimitMap = new HashMap<>();
        qianchuanLimitMap.put("qianchuan01", 100);
        qianchuanLimitMap.put("qianchuan02", 100);
        qianchuanLimitMap.put("qianchuan_gms50b0k0f5", 100);
        qianchuanLimitMap.put("qianchuan_cfwk5hchuhg", 100);
        limitMap.put(RateLimiterConfig.QIANCHUAN.getModule(), qianchuanLimitMap);
    }

    /**
     * 获取令牌，最多等待3秒
     */
    public static boolean getAcquire(RateLimiterConfig module, String key, long timeOutMilliSeconds) {
        RRateLimiter limiter = getRateLimiter(module, key);
        boolean result;
        try {
            result = limiter.tryAcquire(1, timeOutMilliSeconds, TimeUnit.MILLISECONDS);
        } catch (RedisException redisException) {
            log.error("获取令牌失败, 重新设置令牌" + redisException);
            trySetRateLimiter(module, key, limiter);
            result = limiter.tryAcquire(1, timeOutMilliSeconds, TimeUnit.MILLISECONDS);
        }
        return result;
    }

    /**
     * 立即获取令牌
     */
    public static boolean getAcquireAtOnce(RateLimiterConfig module, String key) {
        RRateLimiter limiter = getRateLimiter(module, key);
        boolean result;
        try {
            result = limiter.tryAcquire(1);
        } catch (RedisException redisException) {
            log.error("获取令牌失败, 重新设置令牌" + redisException);
            trySetRateLimiter(module, key, limiter);
            result = limiter.tryAcquire(1);
        }
        return result;
    }

    private static RRateLimiter getRateLimiter(RateLimiterConfig module, String key) {
        if (!limiterMap.containsKey(key)) {
            RRateLimiter rateLimiter = redissonClient.getRateLimiter("rateLimiter:" + module.getModule() + ":" + key);
            trySetRateLimiter(module, key, rateLimiter);
            limiterMap.put(key, rateLimiter);
            return rateLimiter;
        } else {
            return limiterMap.get(key);
        }
    }

    /**
     * 获取限流数量，这里可以可以针对每个module的key做一个数量的适配
     *
     * @param module
     * @param key
     * @return
     */
    private static Integer getLimit(RateLimiterConfig module, String key) {
        Integer limit = module.getLimit();
        Map<String, Integer> moduleLimitMap = limitMap.get(module.getModule());
        if (MapUtil.isNotEmpty(moduleLimitMap)) {
            Integer adapterLimit = moduleLimitMap.get(key);
            if (null != adapterLimit) {
                limit = adapterLimit;
            }
        }
        return limit;
    }

    private static void trySetRateLimiter(RateLimiterConfig module, String key, RRateLimiter rateLimiter) {
        Integer limit = getLimit(module, key);
        rateLimiter.trySetRate(RateType.OVERALL, limit, 1, RateIntervalUnit.SECONDS);
    }

    @Getter
    @AllArgsConstructor
    public enum RateLimiterConfig {

        CLJinniu("CLJinniu", 10),
        QIANCHUAN("qianchuan", 30),
        QIANCHUAN_hzy("qianchuan_hzy", 10),
        QIANCHUAN_SYNC("qianchuan_sync", 50),
        QIANCHUAN_CLIENT("qianchuanClient", 10);

        private final String module;

        private final Integer limit;
    }


    public static class Main {
        public static void main(String[] args) throws InterruptedException {
            RRateLimiter rateLimiter = createLimiter();


            int allThreadNum = 20;

            CountDownLatch latch = new CountDownLatch(allThreadNum);

            long startTime = System.currentTimeMillis();
            for (int i = 0; i < allThreadNum; i++) {
                new Thread(() -> {
                    rateLimiter.acquire(10);
                    System.out.println(Thread.currentThread().getName());
                    latch.countDown();
                }).start();
            }
            latch.await();
            System.out.println("总耗时📶 " + (System.currentTimeMillis() - startTime));
        }

        public static RRateLimiter createLimiter() {
            Config config = new Config();
            config.useSingleServer()
                    .setTimeout(1000000)
                    .setAddress("redis://47.104.105.1:6379")
                    .setPassword("redis1213");

            RedissonClient redisson = Redisson.create(config);
            RRateLimiter rateLimiter = redisson.getRateLimiter("myRateLimiter");
            // 初始化
            // 最大流速 = 每1秒钟产生1个令牌
            rateLimiter.trySetRate(RateType.OVERALL, 30, 1, RateIntervalUnit.SECONDS);
            return rateLimiter;
        }
    }
}
