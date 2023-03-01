package com.fyy.common.tools.config;

import cn.hutool.core.util.StrUtil;
import com.fyy.common.tools.exception.RenException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author fuyouyi
 * @description 静态util初始化到spring容器，用于注入静态变量
 */
public abstract class AutowireInitializingStaticUtil {

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private ModuleConfig moduleConfig;

    /**
     * 注解Autowired 字段可以自动注入;
     * 注解Qualifier 可以指定名称，带:可以跳过检查，随意取（别问代码为什么这么坑爹，这一切要从辣鸡mq写法说起）;
     *
     * @throws IllegalAccessException
     */
    @PostConstruct
    protected void init() throws IllegalAccessException {
        Class<?> clazz = this.getClass();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (isSkip(field)) {
                continue;
            }

            field.setAccessible(true);
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (Modifier.isStatic(field.getModifiers()) && (autowired != null)) {
                Qualifier qualifier = field.getAnnotation(Qualifier.class);
                if (qualifier == null) {
                    field.set(null, applicationContext.getBean(field.getType()));

                } else if (qualifier.value().contains(":")) {
                    Map<String, ?> beans = applicationContext.getBeansOfType(field.getType());
                    if (beans.isEmpty()) {
                        throw new RenException("Bean注册失败" + field.getType());
                    }
                    boolean find = false;
                    for (Map.Entry<String, ?> bean : beans.entrySet()) {
                        if (bean.getKey().equals(qualifier.value().split(":")[0])) {
                            field.set(null, bean.getValue());
                            find = true;
                            break;
                        }
                    }
                    // 没找到就取最后一个
                    if (!find) {
                        field.set(null, beans.entrySet().stream().findFirst().get().getValue());
                    }
                } else {
                    field.set(null, applicationContext.getBean(qualifier.value(), field.getType()));
                }
            }
        }
    }

    private boolean isSkip(Field field) {
        boolean ok = false;
        if ("gateway".equals(moduleConfig.getName())) {
            Annotation[] annotations = field.getType().getDeclaredAnnotations();
            for (Annotation annotation : annotations) {
                if (StrUtil.containsIgnoreCase(annotation.annotationType().getName(), "feign")) {
                    ok = true;
                    break;
                }
            }
        }
        return ok;
    }
}
