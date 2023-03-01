package com.fyy.common.tools.global;

import java.lang.annotation.*;


/**
 * @author fuyouyi
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LoggerManage {

    /**
     * 描述
     */
    String description();

    /**
     * 是否打印返回结果
     */
    boolean printReturning() default true;
}
