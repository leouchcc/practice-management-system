package com.practice.config;

import com.practice.common.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        String msg = e.getMessage();
        if (msg == null || msg.isEmpty()) {
            msg = "运行时异常";
        }
        logger.error("Runtime exception occurred: {}", msg, e);
        return Result.error(msg);
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        String msg = e.getMessage();
        if (msg == null || msg.isEmpty()) {
            msg = "服务器内部错误";
        }
        logger.error("Exception occurred: {}", msg, e);
        return Result.error("服务器内部错误: " + msg);
    }
}