package com.fyy.common.tools.global;

import com.fyy.common.tools.exception.ErrorCode;
import com.fyy.common.tools.utils.HttpContextUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 响应数据
 *
 * @author carl
 * @since 1.0.0
 */
@AllArgsConstructor
@NoArgsConstructor
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 编码：0表示成功，其他值表示失败
     */
    @ApiModelProperty(name = "编码：0表示成功，其他值表示失败")
    private Integer code = 0;
    /**
     * 消息内容
     */
    @ApiModelProperty(name = "消息内容")
    private String msg = "success";

    @ApiModelProperty(name = "RequestId")
    private String requestId;

    /**
     * 响应数据
     */
    @ApiModelProperty(name = "响应数据")
    private T data;

    {
        if (HttpContextUtils.getHttpServletRequest() != null) {
            this.requestId = HttpContextUtils.getHttpServletRequest().getHeader("request_id");
        }
    }

    public Result<T> ok(T data) {
        this.setData(data);
        return this;
    }

    public boolean success() {
        return code == 0;
    }

    public Result<T> error() {
        this.code = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.msg = ErrorCode.getMsgByCode(this.code);
        return this;
    }

    public Result<T> error(int code) {
        this.code = code;
        this.msg = ErrorCode.getMsgByCode(this.code);
        return this;
    }

    public Result<T> error(int code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    public Result<T> error(String msg) {
        this.code = ErrorCode.INTERNAL_SERVER_ERROR.getCode();
        this.msg = msg;
        return this;
    }

    public Result<T> fallback(String msg) {
        this.code = ErrorCode.FALLBACK_ERROR.getCode();
        this.msg = msg;
        return this;
    }

    public boolean fallback() {
        return code.equals(ErrorCode.FALLBACK_ERROR.getCode());
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getRequestId() {
        return requestId;
    }

    public Result<T> setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public static String buildErrorMsg(String param, Result rs) {
        return param + (rs == null ? "" : ": " + rs.getMsg());
    }
}
