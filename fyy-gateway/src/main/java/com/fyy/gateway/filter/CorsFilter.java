package com.fyy.gateway.filter;

import com.alibaba.nacos.common.utils.CollectionUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

/**
 * Cors跨域
 *
 * @author fuyouyi
 */
@Configuration
public class CorsFilter implements WebFilter, Ordered {
    private static final String MAX_AGE = "18000L";

    @Override
    public int getOrder() {
        return -200;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange ctx, WebFilterChain chain)
    {
        ServerHttpRequest request = ctx.getRequest();
//        if (!CorsUtils.isCorsRequest(request)) {
//            return chain.filter(ctx);
//        }

        ServerHttpResponse response = ctx.getResponse();

        addCorsHeaders(request, response);
        if (request.getMethod() == HttpMethod.OPTIONS) {
            response.setStatusCode(HttpStatus.OK);
            return Mono.empty();
        }
        return chain.filter(ctx);
    }

    public static void addCorsHeaders(ServerHttpRequest request, ServerHttpResponse response) {
        HttpHeaders requestHeaders = request.getHeaders();
        HttpMethod requestMethod = requestHeaders.getAccessControlRequestMethod();
        HttpHeaders headers = response.getHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, requestHeaders.getOrigin());
        if (CollectionUtils.isNotEmpty(requestHeaders.getAccessControlRequestHeaders())) {
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, String.join(",", requestHeaders.getAccessControlRequestHeaders().toArray(new String[0])));
        }
        if (requestMethod != null) {
            headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, requestMethod.name());
        }
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);

    }

}
