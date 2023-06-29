package com.kusitms.website.global.exception;

import com.kusitms.website.global.common.BaseExceptionStatus;
import com.kusitms.website.global.common.BaseResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse handleException(RuntimeException ex) {
        return BaseResponse.error(BaseExceptionStatus.INTER_SERVER_ERROR);
    }
}
