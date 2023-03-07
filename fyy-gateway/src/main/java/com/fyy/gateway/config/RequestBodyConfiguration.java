package com.fyy.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @description: 请求体配置类
 * @author: hzy
 * @since: 2020/10/27
 **/
@Configuration
public class RequestBodyConfiguration {
    @Autowired
    private LogRequestBodyGatewayFilterFactory logRequestBodyGatewayFilterFactory;

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("rewrite_json", r -> r.path("/post_json")
                        .and()
                        .readBody(String.class, requestBody -> true)
                        .filters(f -> f.filter(logRequestBodyGatewayFilterFactory.apply(new LogRequestBodyGatewayFilterFactory.Config())))
                        .uri("lb://waiter"))
                .build();
    }
}
