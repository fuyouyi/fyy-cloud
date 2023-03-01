package com.fyy.common.tools.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 采集数据类型
 *
 * @author fyy
 */
@Getter
@AllArgsConstructor
public enum ErrorCode {

    /**
     * 常用异常
     */
    INTERNAL_SERVER_ERROR(500, "内部服务异常|"),

    NOT_NULL(10000, "数据为空|"),

    DB_RECORD_EXISTS(1111, "数据重复提交|"),

    /**
     * 微服务异常
     */
    FALLBACK_ERROR(999, "微服务异常|")
    ;


    /**
     * code
     */
    private final Integer code;
    /**
     * msg
     */
    private final String msg;

    public static String getMsgByCode(Integer code) {
        for (ErrorCode e : ErrorCode.values()) {
            if (e.getCode().equals(code)) {
                return e.getMsg();
            }
        }
        return "内部服务异常";
    }
}