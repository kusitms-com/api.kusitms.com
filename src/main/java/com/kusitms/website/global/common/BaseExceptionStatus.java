package com.kusitms.website.global.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseExceptionStatus {
    SUCCESS(HttpStatus.OK, 200, "요청 성공"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    NOT_SUPPORT_FILE(HttpStatus.BAD_REQUEST, 4001, "지원하지 않는 파일 형식입니다"),
    FILE_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, 4002, "파일 업로드 실패");

    private HttpStatus httpStatus;
    private int code;
    private String message;

    private BaseExceptionStatus(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
