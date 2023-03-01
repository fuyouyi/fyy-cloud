package com.fyy.common.tools.exception;

/**
 * Hystrix降级
 */
public class HystrixException extends RenException {

    public HystrixException(String msg) {
        super(ErrorCode.FALLBACK_ERROR.getCode(), msg);
    }
}
