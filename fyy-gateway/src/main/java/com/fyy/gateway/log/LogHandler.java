package com.fyy.gateway.log;

import cn.hutool.core.util.StrUtil;
import com.fyy.common.tools.log.SysLogOperation;
import com.fyy.common.tools.log.enums.LogTypeEnum;
import com.fyy.gateway.util.ServerWebExchangeUtil;
import com.fyy.security.producer.LogProducer;
import com.fyy.security.user.UserDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @description: 日志处理类
 * @author: hzy
 * @since: 2020/10/26
 **/
@Component
@ConfigurationProperties(prefix = "user.operation.ignore")
public class LogHandler {

    /**
     * token查询不到用户信息时CreatorName 存Null
     */
    public static final String UN_KNOWN_CREATORNAME = "UN_KNOWN";

    /**
     * token查询不到用户信息时Creator 存-1L
     */
    public static final Long NULL_CREATOR = -1L;

    /**
     * 访问渠道请求头
     */
    public static final String CHANNEL_HEADER_NAME = "Channel";


    @Autowired
    private LogProducer logProducer;

//    @Lazy
//    @Autowired
//    private UserOpenFeignClient userOpenFeignClient;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 不拦截的urls
     */
    private List<String> urls;

    /**
     * 不拦截的ip
     */
    private Set<String> ips;

    /**
     * 保存用户行为日志
     * @param exchange
     * @param executeTime
     * @param token
     */
    @Async
    public void saveLog(ServerWebExchange exchange, Long executeTime, String token) {
        if (isIgnore(exchange)) {
            return;
        }
        SysLogOperation log = new SysLogOperation();
        //登录用户信息
        UserDetail user = this.getUserDetailByToken(token);
        if(user != null){
            log.setCreator(user.getId());
            log.setCreatorName(user.getUsername());
        }else {
            log.setCreator(NULL_CREATOR);
            log.setCreatorName(UN_KNOWN_CREATORNAME);
        }

        log.setType(LogTypeEnum.OPERATION.value());
        log.setRequestTime(executeTime.intValue());
        log.setCreateTime(new Date());

        //请求相关信息
        String uri = ServerWebExchangeUtil.getUri(exchange);
        log.setModule(this.getModule(uri));
        log.setIp(ServerWebExchangeUtil.getIpAddr(exchange));
        log.setUserAgent(ServerWebExchangeUtil.getHeader(exchange, HttpHeaders.USER_AGENT));
        log.setChannel(ServerWebExchangeUtil.getHeader(exchange, CHANNEL_HEADER_NAME));
        log.setRequestUri(uri);
        log.setRequestMethod(ServerWebExchangeUtil.getRequestMethod(exchange));
        log.setStatus(ServerWebExchangeUtil.getResponseStatusCode(exchange));
        log.setRequestId(ServerWebExchangeUtil.getHeader(exchange,"request_id"));

        //请求参数
        log.setRequestParams(ServerWebExchangeUtil.getRequestParams(exchange));

        //保存到Redis队列里
        logProducer.saveLog(log);
    }

    /**
     * 判读是否忽略记录该请求
     * @param exchange
     * @return
     */
    private boolean isIgnore(ServerWebExchange exchange) {
        String uri = ServerWebExchangeUtil.getUri(exchange);
        boolean urlIgnore = pathMatcher(uri);
        if (urlIgnore) {
            return true;
        }

        String ipAddr = ServerWebExchangeUtil.getIpAddr(exchange);
        if (ips.contains(ipAddr)) {
            return true;
        }

        /*String channel = ServerWebExchangeUtil.getHeader(exchange, CHANNEL_HEADER_NAME);
        if (StrUtil.isBlank(channel)) {
            return true;
        }*/
        return false;
    }

    /**
     * 路径匹配
     * @param requestUri
     * @return
     */
    private boolean pathMatcher(String requestUri){
        for (String url : urls) {
            if(antPathMatcher.match(url, requestUri)){
                return true;
            }
        }
        return false;
    }

    /**
     * 根据URI截取Module
     * @param uri
     * @return
     */
    private String getModule(String uri) {
        if (StrUtil.isBlank(uri)) {
            return uri;
        }
        try {
            return StrUtil.sub(uri, 1, StrUtil.indexOf(uri, '/', 1));
        }catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据token获取用户信息
     * @param token
     * @return
     */
    private UserDetail getUserDetailByToken(String token) {
//        if (StrUtil.isBlank(token)) {
            return null;
//        }
//        Result<UserDetail> userDetailResult = userOpenFeignClient.getUserDetailByToken(token);
//        if (null == userDetailResult && !userDetailResult.success()) {
//            return null;
//        }
//        return userDetailResult.getData();
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public Set<String> getIps() {
        return ips;
    }

    public void setIps(Set<String> ips) {
        this.ips = ips;
    }
}
