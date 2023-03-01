package com.fyy.common.tools.global;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;


@Aspect
@Component
@Slf4j
public class LoggerAdvice {

    private final ThreadLocal<Long> startTime = new ThreadLocal<>();

    @Before(" @annotation(logManage)")
    public void addBeforeLogger(JoinPoint joinPoint, LoggerManage logManage) {
        log.info("执行{}开始,方法签名:{},传入参数:{}", logManage.description(), joinPoint.getSignature().toShortString(), parseParams(joinPoint));
        startTime.set(System.nanoTime());
    }

    @AfterReturning(returning = "rvt", pointcut = "@annotation(logManage)")
    public void addAfterReturningLogger(LoggerManage logManage, Object rvt) {
        long executeTime = (System.nanoTime() - startTime.get()) / 1000000;
        startTime.remove();
        log.info("执行{}结束,耗时{}ms", logManage.description(), executeTime);
        if (logManage.printReturning() && rvt != null) {
            if (rvt instanceof String) {
                String result = (String) rvt;
                if (StrUtil.isBlank(result)) {
                    return;
                }
            }
            log.info("返回结果:{}", JSONObject.toJSONString(rvt));
        }
    }

    /**
     * 获取参数名和参数值
     * 这里要注意一点，若需要打印父类对象属性，则需子类toString方法中打印父类属性
     * 推荐使用lombok，子类上增加@ToString(callSuper = true)注解即可
     *
     * @param joinPoint
     * @return
     */
    private String parseParams(JoinPoint joinPoint) {
        // 1.这里获取到所有的参数值的数组
        Object[] paramValues = joinPoint.getArgs();
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        // 2.最关键的一步:通过这获取到方法的所有参数名称的字符串数组
        String[] paramNames = methodSignature.getParameterNames();
        Map<String, Object> param = new HashMap<>(16);
        for (int i = 0; i < paramNames.length; i++) {
            //ServletRequest、ServletResponse不能序列化
            if (paramValues[i] instanceof ServletRequest ||
                    paramValues[i] instanceof ServletResponse ||
                    paramValues[i] instanceof HttpSession ||
                    "file".equals(paramNames[i])) {
                continue;
            }
            param.put(paramNames[i], paramValues[i]);
        }
        String result = StrUtil.EMPTY;
        if (!CollectionUtils.isEmpty(param)) {
            try {
                result = JSONObject.toJSONString(param);
            } catch (Exception e) {
                log.error("json转换异常", e);
                result = param.toString();
            }
        }
        return result;
    }
}
