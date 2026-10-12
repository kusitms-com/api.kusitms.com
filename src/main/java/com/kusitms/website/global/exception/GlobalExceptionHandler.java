package com.kusitms.website.global.exception;

import com.kusitms.website.global.common.BaseException;
import com.kusitms.website.global.common.BaseExceptionStatus;
import com.kusitms.website.global.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
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

    // @PreAuthorize 거부 예외가 아래 RuntimeException 핸들러에 잡혀 200으로 내려가지 않도록 먼저 처리한다.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<BaseResponse<BaseException>> handleAccessDenied(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(BaseResponse.error(BaseExceptionStatus.FORBIDDEN));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<BaseResponse<BaseException>> handleException(RuntimeException ex) {
        log.error("Unhandled exception", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.error(BaseExceptionStatus.INTER_SERVER_ERROR));
    }
}
