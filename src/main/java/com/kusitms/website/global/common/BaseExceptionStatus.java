package com.kusitms.website.global.common;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BaseExceptionStatus {
    SUCCESS(HttpStatus.OK, 200, "요청 성공"),
    INTER_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 500, "INTER SERVER ERROR"),
    BAD_REQUEST(HttpStatus.BAD_REQUEST, 400, "잘못된 요청입니다."),
    NOT_SUPPORT_FILE(HttpStatus.BAD_REQUEST, 4001, "지원하지 않는 파일 형식입니다"),
    FILE_UPLOAD_FAIL(HttpStatus.BAD_REQUEST, 4002, "파일 업로드 실패"),

    /* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED, 401, "인증되지 않은 사용자입니다."),
    INVALID_AUTH_TOKEN(HttpStatus.UNAUTHORIZED ,401, "권한 정보가 없는 토큰입니다"),
    EXPIRED_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, 401, "액세스 토큰이 만료되었습니다. 토큰을 재발급해주세요."),
    UNSUPPORTED_AUTH_TOKEN(HttpStatus.UNAUTHORIZED, 401, "지원되지 않는 토큰입니다."),
    WRONG_TOKEN(HttpStatus.UNAUTHORIZED, 401, "잘못된 형식의 토큰입니다."),
    ;

    private HttpStatus httpStatus;
    private int code;
    private String message;

    private BaseExceptionStatus(HttpStatus httpStatus, int code, String message) {
        this.httpStatus = httpStatus;
        this.code = code;
        this.message = message;
    }
}
