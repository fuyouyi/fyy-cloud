package com.fyy.gateway.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.sinopharm.gateway.config.LogRequestBodyGatewayFilterFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @description: ServerWebExchange工具类
 * @author: hzy
 * @since: 2020/10/26
 **/
public class ServerWebExchangeUtil {

    public static final String UNKNOWN = "unknown";
    /**
     * 上传文件content_type
     */
    public static final String CONTENT_TYPE_FILE = "multipart/form-data";

    /**
     * 文件上传设置默认body内容
     */
    public static final String FILE_UPLOAD_DEFAULT_BODY = "{\"file\":\"default_body\"}";

    private static final String IP_LOCAL = "127.0.0.1";
    private static final int IP_LEN = 15;

    /**
     * 获得用户远程ip
     */
    public static String getIpAddr(ServerWebExchange serverWebExchange) {

        String ip = null;

        ip = getHeader(serverWebExchange, "x-forwarded-for");

        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip) ) {
            ip = getHeader(serverWebExchange, "Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || ip.length() == 0 || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = getHeader(serverWebExchange, "WL-Proxy-Client-IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = getHeader(serverWebExchange, "HTTP_CLIENT_IP");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = getHeader(serverWebExchange, "HTTP_X_FORWARDED_FOR");
        }
        if (StrUtil.isBlank(ip) || UNKNOWN.equalsIgnoreCase(ip)) {
            ip = serverWebExchange.getRequest().getRemoteAddress().toString();
        }
        return ip;
    }

    /**
     * 获取客户端真实ip
     * @param  serverWebExchange
     * @return 返回ip
     */
    public static String getIP(ServerWebExchange serverWebExchange) {
        HttpHeaders headers = serverWebExchange.getRequest().getHeaders();
        String ipAddress = headers.getFirst("x-forwarded-for");
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = headers.getFirst("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = headers.getFirst("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
            ipAddress = Optional.ofNullable(serverWebExchange.getRequest().getRemoteAddress())
                    .map(address -> address.getAddress().getHostAddress())
                    .orElse("");
            if (IP_LOCAL.equals(ipAddress)) {
                // 根据网卡取本机配置的IP
                try {
                    InetAddress inet = InetAddress.getLocalHost();
                    ipAddress = inet.getHostAddress();
                } catch (UnknownHostException e) {
                    // ignore
                }
            }
        }

        // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
        if (ipAddress != null && ipAddress.length() > IP_LEN) {
            int index = ipAddress.indexOf(",");
            if (index > 0) {
                ipAddress = ipAddress.substring(0, index);
            }
        }
        return ipAddress;
    }

    /**
     * 从ServerWebExchange获取请求头的值
     * @param serverWebExchange
     * @param headerName
     * @return
     */
    public static String getHeader(ServerWebExchange serverWebExchange, String headerName) {
        String value = null;
        try {
            value = serverWebExchange.getRequest().getHeaders().get(headerName).get(0);
        }catch (Exception e) {

        }
        return value;
    }

    /**
     * 获取URI路径
     * @param exchange
     * @return
     */
    public static String getUri(ServerWebExchange exchange) {
       return exchange.getRequest().getPath().pathWithinApplication().value();
    }

    /**
     * 获取请求方法
     * @param exchange
     * @return
     */
    public static String getRequestMethod(ServerWebExchange exchange) {
        return exchange.getRequest().getMethodValue();
    }

    /**
     * 获取请求参数，封装String 返回。参数分为两部分。一部分为URL上的查询参数，另一部分是body里面的参数。
     * @param exchange
     * @return
     */
    public static String getRequestParams(ServerWebExchange exchange) {
        String json = JSON.toJSONString(getRequestParamsMap(exchange));
        return StrUtil.replace(json, "\\\"","\"");
    }

    /**
     * 获取请求所有参数，封装Map返回。参数分为两部分。一部分为URL上的查询参数，另一部分是body里面的参数。
     * @param exchange
     * @return
     */
    public static Map<String, Object> getRequestParamsMap(ServerWebExchange exchange) {
        Map<String, Object> resultMap = new HashMap<>();
        //URL query 参数获取
        resultMap.put("query", getURLParams(exchange));
        //Body 参数获取
        resultMap.put("body", getBodyJsonObject(exchange));
        return resultMap;
    }


    /**
     * 获取body参数Map
     * @param exchange
     * @return
     */
    public static Object getBodyJsonObject(ServerWebExchange exchange) {
        try {
            String body = getBody(exchange);
            if (StrUtil.isBlank(body)) {
                return null;
            }
            return JSON.parse(body);
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取body参数
     * @param exchange
     * @return
     */
    public static String getBody(ServerWebExchange exchange) {
        //如果上传文件，则直接返回
        List<String> contentTypes = exchange.getRequest().getHeaders().get(HttpHeaders.CONTENT_TYPE);
        if (!CollectionUtils.isEmpty(contentTypes)) {
            for (String value : contentTypes) {
                if (null != value && value.contains(CONTENT_TYPE_FILE)) {
                    return FILE_UPLOAD_DEFAULT_BODY;
                }
            }
        }

        //获取请求体
        return getBodyByAttributeCache(LogRequestBodyGatewayFilterFactory.CACHE_REQUEST_BODY_OBJECT_KEY, exchange);
    }

    /**
     * 通过Attribute里面获取body
     * @param cacheRequestBodyObjectKey
     * @param exchange
     * @return
     */
    private static String getBodyByAttributeCache(String cacheRequestBodyObjectKey, ServerWebExchange exchange) {
        String body = StrUtil.removeAllLineBreaks(exchange.getAttribute(cacheRequestBodyObjectKey));
        return StrUtil.cleanBlank(body);
    }

    /**
     * 通过Flux订阅方式获取body。有坑，慎用
     * @param exchange
     * @return
     */
    public static String getBodyByFluxDataBuffer(ServerWebExchange exchange) {
        Flux<DataBuffer> dataBufferFlux = exchange.getRequest().getBody();
        StringBuilder sb = new StringBuilder();

        dataBufferFlux.subscribe(buffer -> {
            byte[] bytes = new byte[buffer.readableByteCount()];
            buffer.read(bytes);
            DataBufferUtils.release(buffer);
            String bodyString = new String(bytes, StandardCharsets.UTF_8);
            sb.append(bodyString);
        });
        String body = StrUtil.removeAllLineBreaks(sb.toString());
        return StrUtil.cleanBlank(body);
    }

    /**
     * 获取完整URL 请求参数，封装成Map返回
     * @param exchange
     * @return
     */
    private static Map<String, String> getURLParams(ServerWebExchange exchange) {

        MultiValueMap<String, String> queryParamsMap = exchange.getRequest().getQueryParams();
        if (queryParamsMap.isEmpty()) {
            return null;
        }
        Map<String, String> params = new HashMap<>(8);
        queryParamsMap.forEach((key, value) -> {
            String valueStr = null;
            if (!CollectionUtils.isEmpty(value)) {
                if (value.size() == 1) {
                    valueStr = value.get(0);
                }else {
                    valueStr = CollUtil.join(value, "&");
                }
            }
            params.put(key, valueStr);
        });
        return params;
    }

    /**
     * 获取返回响应码
     * @param exchange
     * @return
     */
    public static Integer getResponseStatusCode(ServerWebExchange exchange) {
        return exchange.getResponse().getStatusCode().value();
    }
}
