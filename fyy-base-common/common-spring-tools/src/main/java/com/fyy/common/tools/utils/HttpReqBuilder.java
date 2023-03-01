package com.fyy.common.tools.utils;

import cn.hutool.http.ContentType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于构造 OkHttpClientUtils的参数
 *
 * @author fuyouyi
 * @since 2021/04/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class HttpReqBuilder {

    /**
     * 默认JSON
     */
    private ContentType contentType = ContentType.JSON;

    /**
     * 请求url
     */
    private String url;

    /**
     * Get请求body, params传其一, 优先params
     */
    private Map<String, ?> queryParams;

    /**
     * Get请求body, params传其一, 优先params
     */
    private String jsonBody;

    /**
     * Post请求x-www-form-urlencoded参数
     */
    private Map<String, ?> formParams;

    /**
     * headers
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * 是否关闭复用连接，默认 false
     */
    private Boolean isCloseConnection;

    /**
     * 是否打印日志, 默认 true
     */
    private Boolean isPrintLog;
}
