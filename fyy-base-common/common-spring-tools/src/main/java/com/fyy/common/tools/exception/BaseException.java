package com.fyy.common.tools.exception;


/**
 * 自定义异常
 * 各类自定义异常需继承此类型
 *
 * @author fyy
 */
public abstract class BaseException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;

    public BaseException(int code) {
        super(ErrorCode.getMsgByCode(code));
        this.code = code;
        this.msg = ErrorCode.getMsgByCode(code);
    }

    public BaseException(int code, String errorMsg) {
        super(errorMsg);
        this.code = code;
        this.msg = errorMsg;
    }

    public BaseException(int code, Throwable e) {
        super(e);
        this.code = code;
        this.msg = ErrorCode.getMsgByCode(code);
    }

    public BaseException(String msg) {
        super(msg);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.msg = msg;
    }

    public BaseException(String msg, Throwable e) {
        super(msg, e);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

}