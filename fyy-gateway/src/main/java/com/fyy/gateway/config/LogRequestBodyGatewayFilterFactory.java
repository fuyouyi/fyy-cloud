package com.fyy.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

/**
 * @description: 日志请求体自定义过滤器
 * @author: hzy
 * @since: 2020/10/27
 **/
@Slf4j
@Component
public class LogRequestBodyGatewayFilterFactory extends
        AbstractGatewayFilterFactory<LogRequestBodyGatewayFilterFactory.Config> {

    public static final String CACHE_REQUEST_BODY_OBJECT_KEY = "cachedRequestBodyObject";

    public LogRequestBodyGatewayFilterFactory() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String requestBody = exchange.getAttribute(CACHE_REQUEST_BODY_OBJECT_KEY);
            log.info(requestBody);
            return chain.filter(exchange);
        };
    }

    public static class Config {
    }
}