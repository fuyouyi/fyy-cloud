package com.fyy.security.redis;

import com.fyy.security.redis.serializer.JsonRedisSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Redis配置
 *
 * @author carl
 * @since 1.0.0
 */
@Configuration
public class RedisConfig {

    @Resource
    private RedisConnectionFactory factory;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new JsonRedisSerializer<>(Object.class));
        redisTemplate.setConnectionFactory(factory);

        return redisTemplate;
    }

    @Bean
    public RedisUtils redisUtils(){
        return new RedisUtils();
    }

    @Bean
    public RedisCacheManager redisCacheManager(RedisTemplate redisTemplate) {
        // 针对不同cacheName，设置不同的过期时间。写多个put方法就行。
        Map<String, RedisCacheConfiguration> confMap = new HashMap<>(8);
        confMap.put(RedisKeys.SYS_DICT_TYPE_KEY, this.buildRedisCacheConfig(redisTemplate, Duration.ofMinutes(60)));
        confMap.put(RedisKeys.DEVICE_MONITOR_KEY, this.buildRedisCacheConfig(redisTemplate, Duration.ofDays(1)));
        confMap.put(RedisKeys.MONITOR_CONFIG_KEY, this.buildRedisCacheConfig(redisTemplate, Duration.ofDays(1)));
        confMap.put(RedisKeys.MONITOR_TYPE_KEY, this.buildRedisCacheConfig(redisTemplate, Duration.ofDays(1)));
        confMap.put(RedisKeys.VARIABLE_EXIST_KEY, this.buildRedisCacheConfig(redisTemplate, Duration.ofDays(3)));
        confMap.put(RedisKeys.REPORT_PRODUCT_REPORT, this.buildRedisCacheConfig(redisTemplate, Duration.ofMinutes(2)));
        confMap.put(RedisKeys.REPORT_CUSTOMER_REPORT, this.buildRedisCacheConfig(redisTemplate, Duration.ofMinutes(2)));
        confMap.put(RedisKeys.REPORT_CUSTOMER_BOARD_LIST, this.buildRedisCacheConfig(redisTemplate, Duration.ofMinutes(2)));
        confMap.put(RedisKeys.REPORT_AREA_SELL_CUSTOMER_DETAIL, this.buildRedisCacheConfig(redisTemplate, Duration.ofMinutes(2)));

        RedisCacheManager redisCacheManager = RedisCacheManager.builder(redisTemplate.getConnectionFactory())
                // 默认配置（强烈建议配置上）。  比如动态创建出来的都会走此默认配置
                .cacheDefaults(this.buildRedisCacheConfig(redisTemplate, Duration.ofHours(1)))
                // 不同cache的个性化配置
                .withInitialCacheConfigurations(confMap)
                .build();
        return redisCacheManager;
    }

    /**
     * 封装RedisCacheConfiguration 参数
     * @param redisTemplate
     * @return
     */
    private RedisCacheConfiguration buildRedisCacheConfig(RedisTemplate redisTemplate, Duration duration) {
        return RedisCacheConfiguration.defaultCacheConfig()
                // 设置key为String
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getStringSerializer()))
                // 设置value 为自动转Json的Object
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(redisTemplate.getValueSerializer()))
                // 不缓存null
                .disableCachingNullValues()
                .entryTtl(duration);
    }
}