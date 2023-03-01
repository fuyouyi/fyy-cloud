package com.fyy.common.swagger.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import({com.fyy.common.swagger.SwaggerAutoConfiguration.class})
public @interface EnableCustomSwagger2 {

}