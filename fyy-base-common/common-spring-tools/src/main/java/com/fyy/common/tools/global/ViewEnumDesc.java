package com.fyy.common.tools.global;

import java.lang.annotation.*;

/**
 * @author fuyouyi
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ViewEnumDesc {

    Class<?> enumClass();
}
