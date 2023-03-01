package com.fyy.common.tools.feignconfig;

import cn.hutool.core.collection.CollUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashSet;

/**
 * Feign调用，携带header
 *
 * @author carl
 * @since 1.0.0
 */
@Configuration
public class FeignConfig implements RequestInterceptor {

    private static final HashSet<String> HEADER_FILTER_LIST = CollUtil.newHashSet("content-type", "content-length", "connection");

    @Override
    public void apply(RequestTemplate template) {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            return;
        }

        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                // contentType, content-length不向下传递( 传递get请求content-length不为空的时候，会卡死 )
                if (!HEADER_FILTER_LIST.contains(name)) {
                    Enumeration<String> values = request.getHeaders(name);
                    while (values.hasMoreElements()) {
                        String value = values.nextElement();
                        template.header(name, value);
                    }
                }
            }
        }

    }
}