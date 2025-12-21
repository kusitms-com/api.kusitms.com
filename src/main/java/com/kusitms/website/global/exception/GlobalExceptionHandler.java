package com.kusitms.website.global.exception;

import com.kusitms.website.global.common.BaseException;
import com.kusitms.website.global.common.BaseExceptionStatus;
import com.kusitms.website.global.common.BaseResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<BaseResponse<BaseException>> handleIllegalArgument(
            IllegalArgumentException ex
    ) {
        return ResponseEntity
                .badRequest()
                .body(BaseResponse.error(
                        BaseExceptionStatus.BAD_REQUEST,
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse handleException(RuntimeException ex) {
        return BaseResponse.error(BaseExceptionStatus.INTER_SERVER_ERROR);
    }
}
