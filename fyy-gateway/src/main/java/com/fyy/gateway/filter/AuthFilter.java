/**
 * Copyright (c) 2018 商城开源 All rights reserved.
 * <p>
 * <p>
 * <p>
 * 版权所有，侵权必究！
 */

package com.fyy.gateway.filter;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.fyy.common.tools.constant.Constant;
import com.fyy.common.tools.global.Result;
import com.fyy.gateway.feign.UserOpenFeignClientHolder;
import com.fyy.security.user.UserDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Future;

/**
 * 权限过滤器
 *
 * @author carl
 * @since 1.0.0
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "mall")
public class AuthFilter implements GlobalFilter, Ordered
{
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    @Lazy
    @Autowired
    private UserOpenFeignClientHolder userOpenFeignClientHolder;
    /**
     * 不拦截的urls
     */
    private List<String> urls;

    @Override
    public int getOrder() {
        return -100;
    }

    /**
     * // token 及 权限验证结果   |  放行请求的情况下                |  鉴权请求的情况下
     * // token 为空            |  允许继续请求                   |  禁止请求，异常码10099
     * // token 非空 但 解析失败  |  允许继续请求                   |  禁止请求、返回失败信息
     * // token 非空 且 解析成功  |  请求头写入userId 允许继续请求    |  允许继续请求
     * 解析失败，包含这几种情况：token为空/过期/非法、用户不存在、无权访问
     * @return
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String requestUri = request.getPath().pathWithinApplication().value();

        String s = UUID.randomUUID().toString();
        exchange.mutate().request(exchange.getRequest().mutate().header("request_id", new String[]{s.substring(s.lastIndexOf("-")).replace("-", "")}).build());

        boolean urlNoAuth = pathMatcher(requestUri); // 当前请求是否放行
        //获取用户token
        String token = request.getHeaders().getFirst(Constant.TOKEN_HEADER);
        if (StrUtil.isBlank(token)) {
            token = request.getQueryParams().getFirst(Constant.TOKEN_HEADER);
        }
        // 放行请求的情况下，token 为空，允许继续请求
        if (StrUtil.isBlank(token) && urlNoAuth) {
            return chain.filter(exchange);
        }
        //资源访问权限
        String language = request.getHeaders().getFirst(HttpHeaders.ACCEPT_LANGUAGE);
        Future<Result<UserDetail>> futureResult = userOpenFeignClientHolder.resource(language, token, requestUri, request.getMethod().toString());
        Result<UserDetail> result;
        try {
            result = futureResult.get();
        } catch (Exception e) {
            log.error("鉴权服务异常, {}", e.getMessage(), e);
            return response(exchange, new Result<>().error("访问失败"));
        }

        //没权限访问，直接返回
        if (!result.success()) {
            if (urlNoAuth) {
                return chain.filter(exchange);
            } else {
                return response(exchange, result);
            }
        }
        //获取用户信息
        UserDetail userDetail = result.getData();
        if (userDetail != null) {
            //当前登录用户userId，添加到header中
            String id = userDetail.getId() + "";
            ServerHttpRequest build = exchange.getRequest().mutate().header(Constant.USER_KEY, id).build();
            return chain.filter(exchange.mutate().request(build).build());
        }
        return chain.filter(exchange);
    }

    private Mono<Void> response(ServerWebExchange exchange, Object object) {
        String json = JSON.toJSONString(object);
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        DataBuffer buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        response.setStatusCode(HttpStatus.OK);
        CorsFilter.addCorsHeaders(request, response);
        return response.writeWith(Flux.just(buffer));
    }

    private boolean pathMatcher(String requestUri) {
        for (String url : urls) {
            if (antPathMatcher.match(url, requestUri)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}