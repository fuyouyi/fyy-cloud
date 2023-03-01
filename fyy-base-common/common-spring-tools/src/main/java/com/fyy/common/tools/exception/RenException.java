package com.fyy.common.tools.exception;


/**
 * 自定义异常
 *
 * @author fyy
 * @since 2023/02/28
 */
public class RenException extends BaseException {

    public RenException(int code) {
        super(code);
    }

    public RenException(int code, String errorMsg) {
        super(code, errorMsg);
    }

    public RenException(int code, Throwable e) {
        super(code, e);
    }

    public RenException(String msg) {
        super(msg);
    }

    public RenException(String msg, Throwable e) {
        super(msg, e);
    }

    public static RenException newNullException() {
        return new RenException("查询为空");
    }
}