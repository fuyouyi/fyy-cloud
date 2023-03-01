package com.fyy.common.tools.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 用于构造 OkHttpClientUtils的参数
 *
 * @author fyy
 * @since 2023/02/28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class DoGetReqBuilder {

    /**
     * 请求url
     */
    private String url;

    /**
     * body, params传其一, 优先params
     */
    private Map<String, ?> params;

    /**
     * body, params传其一, 优先params
     */
    private String jsonBody;

    /**
     * headers
     */
    private Map<String, String> headers;

    /**
     * 是否关闭复用连接，默认 false
     */
    private Boolean isCloseConnection;

    /**
     * 是否打印日志, 默认 true
     */
    private Boolean isPrintLog;
}
