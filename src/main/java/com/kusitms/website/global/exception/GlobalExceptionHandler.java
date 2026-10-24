package com.kusitms.website.global.exception;

import com.kusitms.website.global.common.BaseException;
import com.kusitms.website.global.common.BaseExceptionStatus;
import com.kusitms.website.global.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

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

    // enum 쿼리/바디 파싱 실패 등이 RuntimeException 핸들러에 잡혀 500이 되지 않도록 400으로 처리한다.
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<BaseResponse<BaseException>> handleBadRequest(Exception ex) {
        return ResponseEntity.badRequest()
                .body(BaseResponse.error(BaseExceptionStatus.BAD_REQUEST));
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
