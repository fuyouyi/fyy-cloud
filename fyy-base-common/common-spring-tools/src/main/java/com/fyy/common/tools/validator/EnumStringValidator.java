package com.fyy.common.tools.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class EnumStringValidator implements ConstraintValidator<EnumValid, String> {

    // 枚举校验注解
    private EnumValid annotation;

    @Override
    public void initialize(EnumValid constraintAnnotation) {

        annotation = constraintAnnotation;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext constraintValidatorContext) {
        boolean result = false;

        Class<?> cls = annotation.target();
        boolean ignoreEmpty = annotation.ignoreEmpty();

        // target为枚举，并且value有值，或者不忽视空值，才进行校验
        if (cls.isEnum() && (value != null || !ignoreEmpty)) {

            Object[] objects = cls.getEnumConstants();
            for (Object obj : objects) {
                // 使用此注解的枚举类需要重写toString方法，改为需要验证的值
                if (obj.toString().equals(String.valueOf(value))) {
                    result = true;
                    break;
                }
            }
        } else {
            result = true;
        }
        return result;
    }
}