package com.aiyichen.admindemo.exception;

import com.aiyichen.admindemo.utils.BaseResponse;
import com.aiyichen.admindemo.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
// 用于定义全局异常处理 当Spring应用中抛出异常时，RestControllerAdvice注解的类会捕获并处理这些异常，然后返回适当的http相应
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    public BaseResponse businessExceptionHandler(BusinessException e){
        log.error("businessException:"+e.getMessage(),e);
        return ResultUtil.error(e.getCode(),e.getMessage(),e.getDescription());
    }
}
