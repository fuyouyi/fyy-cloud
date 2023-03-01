package com.fyy.common.tools.utils;


import cn.hutool.core.util.StrUtil;

/**
 * 正则util
 */
public class StrMatchUtil {
    private StrMatchUtil() {
    }

    /**
     * 手机号格式校验正则
     */
    public static final String PHONE_REGEX = "^1(3\\d|4[5-9]|5[0-35-9]|6[567]|7[0-8]|8\\d|9[0-35-9])\\d{8}$";

    /**
     * 手机号脱敏筛选正则
     */
    public static final String PHONE_BLUR_REGEX = "(\\d{3})\\d{4}(\\d{4})";

    /**
     * 手机号脱敏替换正则
     */
    public static final String PHONE_BLUR_REPLACE_REGEX = "$1****$2";

    /**
     * 1. 长度>3
     * 2. 头尾必须是字母
     * 3. 中间只有字母或者字符串
     */
    public static final String VARIABLE_KEY_REGEX = "^[a-zA-Z][a-zA-Z_]+[a-zA-Z]$";

    /**
     * 1. 头必须是字母
     * 3. 仅支持字母数字和下划线
     */
    public static final String DEVICE_SERIAL_NUMBER_REGEX = "^[a-zA-Z]\\w+$";

    /**
     * 手机号格式校验
     *
     * @param phone
     * @return
     */
    public static boolean checkPhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return false;
        }
        return phone.matches(PHONE_REGEX);
    }

    /**
     * 手机号脱敏处理
     *
     * @param phone
     * @return
     */
    public static String blurPhone(String phone) {
        if (StrUtil.isBlank(phone)) {
            return phone;
        }
        return phone.replaceAll(PHONE_BLUR_REGEX, PHONE_BLUR_REPLACE_REGEX);
    }

    public static void main(String[] args) {
        if (!"sd".matches(StrMatchUtil.VARIABLE_KEY_REGEX)) {
            System.out.println("错了");
        }
    }
}
