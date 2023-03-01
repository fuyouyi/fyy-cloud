package com.fyy.common.tools.global;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;


/**
 * @description 全局处理返回结果
 * @date 2019/7/01 15:28
 */
@ControllerAdvice(annotations = RestController.class)
public class ResponseAdvisor implements ResponseBodyAdvice<Object> {

    /**
     * 过滤哪些接口需要统一返回
     *
     * @param returnType
     * @param converterType
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 统一返回体
     *
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (body instanceof String ||
                body instanceof Result ||
                body instanceof ResponseAdvisorIgnore) {
            return body;
        }
        try {
            String requestURI = ((ServletServerHttpRequest) request).getServletRequest().getRequestURI();
            if (requestURI.contains("/v2/api-docs")) {
                return body;
            }
        } catch (Exception e) {

        }
        return new Result<>().ok(body);
//        return new Result<>().ok( ResultEnumToDescUtil.beforeBodyWrite(body) );
    }

}