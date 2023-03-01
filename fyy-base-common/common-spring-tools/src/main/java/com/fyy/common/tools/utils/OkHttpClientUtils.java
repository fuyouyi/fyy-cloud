package com.fyy.common.tools.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fyy.common.tools.dto.DoGetReqBuilder;
import com.fyy.common.tools.exception.RenException;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Slf4j
public class OkHttpClientUtils {

    private static final Integer TIMEOUT_SECONDS = 60;

    private final OkHttpClient client;

    private OkHttpClientUtils(OkHttpClient client) {
        this.client = client;
    }

    /**
     * 初始化普通实例
     *
     * @return 返回工具类
     */
    public static OkHttpClientUtils getInstance() {
        return OkHttpClientInit.INSTANCE.getInstance();
    }

    /**
     * 枚举单例
     *
     * @author fuyouyi
     */
    public enum OkHttpClientInit {
        INSTANCE;

        /**
         * 单例注册okHttpClientUtil
         */
        private final OkHttpClientUtils okHttpClientUtils;

        OkHttpClientInit() {
            // http连接池设置
            ConnectionPool connectionPool = new ConnectionPool(10, 5, TimeUnit.MILLISECONDS);

            // 创建 OkHttpClient
            okHttpClientUtils = new OkHttpClientUtils(
                    new OkHttpClient.Builder()
                            .protocols(ListUtil.toList(Protocol.HTTP_1_1))
                            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SECONDS * 2, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .connectionPool(connectionPool)
                            .build());
        }

        private OkHttpClientUtils getInstance() {
            return okHttpClientUtils;
        }

    }

    /**
     * [Get] [no param]
     */
    @Deprecated
    public String doGet(String uri) {
        return doGet(HttpReqBuilder.builder().url(uri).build());
    }

    /**
     * [Get] [param]
     */
    @Deprecated
    public String doGet(String uri, Map<String, ?> queryMap) {
        return doGet(HttpReqBuilder.builder().url(uri).queryParams(queryMap).build());
    }


    /**
     * get请求统一入口
     *
     * @param doGetReqBuilder
     * @return
     */
    public String doGet(DoGetReqBuilder doGetReqBuilder) {
        Map<String, ?> paramMap = new HashMap<>();
        if (MapUtil.isNotEmpty(doGetReqBuilder.getParams())) {
            paramMap = doGetReqBuilder.getParams();
        } else if (StrUtil.isNotBlank(doGetReqBuilder.getJsonBody())) {
            paramMap = JSONObject.parseObject(doGetReqBuilder.getJsonBody());
        }
        log.info("doGet [{}] param [{}] - start", doGetReqBuilder.getUrl(), paramMap);
        // 构建httpUrl和param
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(doGetReqBuilder.getUrl())).newBuilder();
        for (Entry<String, ?> queryEntry : paramMap.entrySet()) {
            builder.addQueryParameter(queryEntry.getKey(), String.valueOf(queryEntry.getValue()));
        }
        HttpUrl httpUrl = builder.build();
        // 添加header
        Request.Builder requestBuilder = new Request.Builder().url(httpUrl);
        if (MapUtil.isNotEmpty(doGetReqBuilder.getHeaders())) {
            for (Entry<String, String> headerEntry : doGetReqBuilder.getHeaders().entrySet()) {
                requestBuilder.addHeader(headerEntry.getKey(), headerEntry.getValue());
            }
        }
        if (Boolean.TRUE.equals(doGetReqBuilder.getIsCloseConnection())) {
            requestBuilder.addHeader("Connection", "close");
        }
        Request request = requestBuilder.build();
        return execute(client, doGetReqBuilder.getUrl(), JSON.toJSONString(paramMap), request, doGetReqBuilder.getIsPrintLog());
    }

    /**
     * get请求统一入口
     *
     * @param httpReqBuilder
     */
    public String doGet(HttpReqBuilder httpReqBuilder) {
        Map<String, ?> queryParam = new HashMap<>();
        if (CollUtil.isNotEmpty(httpReqBuilder.getQueryParams())) {
            queryParam = httpReqBuilder.getQueryParams();
        } else if (StrUtil.isNotBlank(httpReqBuilder.getJsonBody())) {
            queryParam = JSONObject.parseObject(httpReqBuilder.getJsonBody());
        }
        log.info("doGet 【{}】 param 【{}】 - start", httpReqBuilder.getUrl(), queryParam);

        // 构建httpUrl
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(httpReqBuilder.getUrl())).newBuilder();
        // 组装queryParam
        for (Entry<String, ?> queryEntry : queryParam.entrySet()) {
            builder.addQueryParameter(queryEntry.getKey(), String.valueOf(queryEntry.getValue()));
        }
        // 构建httpRequestBuilder
        HttpUrl httpUrl = builder.build();
        Request.Builder requestBuilder = new Request.Builder().url(httpUrl);
        // 组装header
        this.addHeaders(requestBuilder, httpReqBuilder.getHeaders());

        if (Boolean.TRUE.equals(httpReqBuilder.getIsCloseConnection())) {
            requestBuilder.addHeader("Connection", "close");
        }
        Request request = requestBuilder.build();
        return execute(client, httpReqBuilder.getUrl(), JSON.toJSONString(queryParam), request, httpReqBuilder.getIsPrintLog());
    }

    /**
     * post请求统一入口
     *
     * @param httpReqBuilder
     */
    public String doPost(HttpReqBuilder httpReqBuilder) {
        Map<String, ?> queryParam = new HashMap<>();
        if (CollUtil.isNotEmpty(httpReqBuilder.getQueryParams())) {
            queryParam = httpReqBuilder.getQueryParams();
        }
        log.info("doPost 【{}】 queryParam 【{}】 jsonParam 【{}】 - start", httpReqBuilder.getUrl(), queryParam, httpReqBuilder.getJsonBody());

        // 构建httpUrl
        HttpUrl.Builder builder = Objects.requireNonNull(HttpUrl.parse(httpReqBuilder.getUrl())).newBuilder();
        // 组装queryParam
        for (Entry<String, ?> queryEntry : queryParam.entrySet()) {
            builder.addQueryParameter(queryEntry.getKey(), String.valueOf(queryEntry.getValue()));
        }
        // 构建httpRequestBuilder
        HttpUrl httpUrl = builder.build();
        Request.Builder requestBuilder = new Request.Builder().url(httpUrl);

        // 组装header
        if (Boolean.TRUE.equals(httpReqBuilder.getIsCloseConnection())) {
            httpReqBuilder.getHeaders().put("Connection", "close");
        }
        this.addHeaders(requestBuilder, httpReqBuilder.getHeaders());

        // JSON请求 设置mediaType + 添加body
        switch (httpReqBuilder.getContentType()) {
            case JSON:
                MediaType mediaType = MediaType.parse("application/json; charset=utf-8");
                RequestBody body = RequestBody.create(mediaType, httpReqBuilder.getJsonBody());
                requestBuilder.post(body);
                break;
            case FORM_URLENCODED:
                FormBody.Builder formBodyBuilder = new FormBody.Builder();
                for (Entry<String, ?> formEntry : httpReqBuilder.getFormParams().entrySet()) {
                    formBodyBuilder.add(formEntry.getKey(), String.valueOf(formEntry.getValue()));
                }
                FormBody formBody = formBodyBuilder.build();
                requestBuilder.post(formBody);
                break;
            default:
                throw new RenException("不支持的content-type");
        }

        Request request = requestBuilder.build();
        return execute(client, httpReqBuilder.getUrl(), JSON.toJSONString(queryParam), request, httpReqBuilder.getIsPrintLog());
    }

    private void addHeaders(Request.Builder builder, Map<String, String> headers) {
        if (CollUtil.isNotEmpty(headers)) {
            Set<Entry<String, String>> headerList = headers.entrySet();
            for (Entry<String, String> header : headerList) {
                builder = builder.addHeader(header.getKey(), header.getValue());
            }
        }
    }

    /**
     * [POST] [text/html] 带文件的post请求
     */
    public String doPostWithFile(OkHttpClient client, File file, String uri, Map<String, ?> paramMap, Map<String, String> headers, String fileKey) {
        log.info("doPostWithFile param 【{}】 to 【{}】 - start", paramMap, uri);
        MediaType fileMediaType = MediaType.parse("text/html;charset=utf-8");
        RequestBody fileBody = RequestBody.create(fileMediaType, file);
        MultipartBody.Builder multiBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart(fileKey, "", fileBody);
        for (String key : paramMap.keySet()) {
            multiBuilder.addFormDataPart(key, String.valueOf(paramMap.get(key)));
        }
        RequestBody requestBody = multiBuilder.build();
        Request.Builder builder = new Request.Builder();
        this.addHeaders(builder, headers);
        Request request = builder.url(uri).post(requestBody).build();
        return execute(client, uri, JsonUtil.toJson(paramMap), request, true);
    }

    /**
     * [POST] [multipart/form-data] multipart form-data表单， 带headers
     */
    public String doPostMultipartForm(String uri, Map<String, Object> param, Map<String, String> headers) {
        log.info("doPostMultipartForm 【{}】 param 【{}】 headers 【{}】 - start", uri, param, headers);
        MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
        for (Entry<String, Object> entry : param.entrySet()) {
            if (entry.getValue() instanceof File) {
                File file = (File) entry.getValue();
                RequestBody fileBody = RequestBody.create(MediaType.parse("application/octet-stream"), file);
                multipartBodyBuilder.addFormDataPart(entry.getKey(), file.getName(), fileBody);
            } else {
                multipartBodyBuilder.addFormDataPart(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        MultipartBody multipartBody = multipartBodyBuilder.build();
        Request.Builder builder = new Request.Builder();
        this.addHeaders(builder, headers);
        Request request = builder.url(uri).post(multipartBody).build();
        return execute(client, uri, JSON.toJSONString(param), request, true);
    }

    private String execute(OkHttpClient httpClient, String uri, String jsonParam, Request request, Boolean printBodyLog) {
        TimeInterval timer = DateUtil.timer();
        String resultBody;
        Response response = null;
        try {
            response = httpClient.newCall(request).execute();
            if (HttpStatus.SC_OK != response.code()) {
                String strformat = StrUtil.format("httpClientExecute bad response 【{}】 errorMessage 【{}】 url 【{}】 param 【{}】",
                        response.code(), response.message(), uri, jsonParam);
                log.error(strformat);
                throw new RenException(strformat);
            }
            resultBody = response.body().string();
        } catch (IOException e) {
            log.error("httpClientExecute 【{}】 param 【{}】 cost 【{}ms】 errorMessage 【{}】", uri, jsonParam, timer.intervalMs(), e);
            throw new RenException("远程服务异常, 【" + uri + "】, " + e.getMessage(), e);
        } finally {
            if (response != null) {
                try {
                    response.close();
                } catch (Exception e) {
                    // ignore
                }
            }
        }
        if (Boolean.TRUE.equals(printBodyLog) || printBodyLog == null) {
            log.info("httpClientExecute [{}], param[{}], code [{}] cost [{}ms] body [{}] ", uri, jsonParam, response.code(), timer.intervalMs(), resultBody);
        } else {
            log.info("httpClientExecute [{}], param[{}], code [{}] cost [{}ms]", uri, jsonParam, response.code(), timer.intervalMs());
        }
        return resultBody;
    }

    public static void main(String[] args) {
        Map<String, String> accessTokenHeader = new HashMap<>();
    }
}
