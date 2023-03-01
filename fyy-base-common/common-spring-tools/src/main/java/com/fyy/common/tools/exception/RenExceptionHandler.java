package com.fyy.common.tools.exception;

import com.fyy.common.tools.global.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yaml.snakeyaml.constructor.DuplicateKeyException;


/**
 * 异常处理器
 *
 * @author carl
 * @since 1.0.0
 */
@RestControllerAdvice
public class RenExceptionHandler {

    private static final String COMMON_MESSAGE = "系统异常，请稍后再试";
    private static final String NOT_PERMISSION_MESSAGE = "无权限，请联系客服";

    private static final Logger logger = LoggerFactory.getLogger(RenExceptionHandler.class);

    @Autowired(required = false)
    private ExceptionLoger exceptionLoger;

    /**
     * 处理所有不可知异常
     *
     * @param e 异常
     * @return json结果
     */
    @ExceptionHandler(Throwable.class)
    @ResponseBody
    public Result<?> handleException(Throwable e) {
        logger.error(e.getMessage(), e);
        return new Result<>().error(e.getMessage());
    }

    /**
     * 处理所有校验异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public Result<?> handleIllegalArgumentException(IllegalArgumentException e) {
        // 打印异常堆栈信息
        logger.warn(e.getMessage(), e);
        return new Result<>().error(e.getMessage());
    }

    /**
     * 处理自定义异常
     */
    @ExceptionHandler(RenException.class)
    public Result<?> handleRenException(RenException ex) {
        logger.error(ex.getMsg(), ex);
        return new Result<>().error(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public Result<?> handleDuplicateKeyException(DuplicateKeyException ex) {
        return new Result<>().error(ErrorCode.DB_RECORD_EXISTS.getCode());
    }
}