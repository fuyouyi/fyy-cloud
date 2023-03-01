package com.fyy.common.tools.feignconfig;

import feign.Request;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.TimeUnit;

/**
 * 1. 使用负载均衡器后, feignClient的name是固定的, 不能随意更变
 * 2. 使用负载均衡器后，多配置的feignClient需要在java文件配置，并设置 default-to-properties: false
 */
public class FileFeignConfig {

    public static final int CONNECT_TIME_OUT_MILLIS = 5 * 1000;
    public static final int READ_TIME_OUT_MILLIS = 300 * 1000;

    //全局超时配置
    @Bean
    public Request.Options options() {
        return new Request.Options(CONNECT_TIME_OUT_MILLIS, TimeUnit.MILLISECONDS, READ_TIME_OUT_MILLIS, TimeUnit.MILLISECONDS, true);
    }
}
